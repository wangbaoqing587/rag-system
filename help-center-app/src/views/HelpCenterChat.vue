<template>
  <div class="page">
    <ChatHeader :user="user" @switch-user="$emit('switch-user')" />
    <div ref="scrollEl" class="messages">
      <WelcomeMessage :timestamp="welcomeTs" />
      <template v-for="(item, idx) in items" :key="item.id">
        <ChatMessage v-if="item.type === 'chat'" :message="item" />
        <TicketEntryMessage
          v-else-if="item.type === 'ticket_entry'"
          :timestamp="item.timestamp"
          @submit-ticket="openTicket(item.suggestedDescription)"
        />
        <div v-else-if="item.type === 'system'" class="system">{{ item.content }}</div>
      </template>
    </div>
    <div class="bottom">
      <FaqQuickChips @pick="sendUserMessage" />
      <ChatInput :disabled="loading" :streaming="streaming" @send="sendUserMessage" @stop="stopStream" />
    </div>
    <TicketSubmitSheet
      :visible="sheetVisible"
      :user="user"
      :chat-id="chatId"
      :suggested-description="pendingSuggested"
      :chat-transcript="transcriptForSubmit"
      @close="sheetVisible = false"
      @success="onTicketSuccess"
    />
    <div v-if="toast" class="toast">{{ toast }}</div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import ChatHeader from '../components/ChatHeader.vue'
import WelcomeMessage from '../components/WelcomeMessage.vue'
import ChatMessage from '../components/ChatMessage.vue'
import TicketEntryMessage from '../components/TicketEntryMessage.vue'
import FaqQuickChips from '../components/FaqQuickChips.vue'
import ChatInput from '../components/ChatInput.vue'
import TicketSubmitSheet from '../components/TicketSubmitSheet.vue'
import { streamChat } from '../api/helpCenter.js'
import { getOrCreateChatId } from '../utils/mockUser.js'

const TICKET_OFFER_MARKER = '[TICKET_OFFER]'

const props = defineProps({
  user: { type: Object, required: true },
})

defineEmits(['switch-user'])

const chatId = ref(getOrCreateChatId())
const welcomeTs = ref(new Date().toISOString())
const items = ref([])
const loading = ref(false)
const streaming = ref(false)
const scrollEl = ref(null)
const abortStream = ref(null)
const sheetVisible = ref(false)
const pendingSuggested = ref('')
const toast = ref('')
let idSeq = 0
let activeStreamGen = 0

const transcriptForSubmit = computed(() => {
  const list = [
    {
      role: 'assistant',
      content:
        '你好，我是帮助中心智能助手。可解答功能用法、计费与常见故障；未命中文档时可提交工单由人工跟进。',
      timestamp: welcomeTs.value,
    },
  ]
  for (const item of items.value) {
    if (item.type === 'chat') {
      list.push({
        role: item.role,
        content: item.content,
        timestamp: item.timestamp,
      })
    } else if (item.type === 'ticket_entry') {
      list.push({
        role: 'assistant',
        content: item.content || '抱歉，我暂时给不了更合适的指引。如果需要人工协助，提交反馈后客服会继续跟进。',
        timestamp: item.timestamp,
      })
    }
  }
  return list
})

onMounted(scrollBottom)

function scrollBottom() {
  nextTick(() => {
    if (scrollEl.value) scrollEl.value.scrollTop = scrollEl.value.scrollHeight
  })
}

function showToast(msg) {
  toast.value = msg
  setTimeout(() => {
    toast.value = ''
  }, 2500)
}

function compactText(text) {
  return (text || '').replace(/\s+/g, '')
}

function isTicketOfferText(text) {
  return compactText(text) === TICKET_OFFER_MARKER
}

function isTicketOfferPrefix(text) {
  const normalized = compactText(text)
  return normalized.length > 0 && TICKET_OFFER_MARKER.startsWith(normalized)
}

function sendUserMessage(text) {
  if (loading.value || !text?.trim()) return
  const userMsg = {
    id: ++idSeq,
    type: 'chat',
    role: 'user',
    content: text.trim(),
    timestamp: new Date().toISOString(),
  }
  items.value.push(userMsg)
  scrollBottom()
  startAssistantReply(text.trim())
}

function startAssistantReply(message) {
  const gen = ++activeStreamGen
  abortStream.value?.()
  for (const item of items.value) {
    if (item.type === 'chat' && item.role === 'assistant' && item.streaming) {
      item.streaming = false
    }
  }
  loading.value = true
  streaming.value = true
  let assistantId = null

  const ensureAssistant = () => {
    if (assistantId == null) {
      const row = {
        id: ++idSeq,
        type: 'chat',
        role: 'assistant',
        content: '',
        timestamp: new Date().toISOString(),
        streaming: true,
        sources: [],
      }
      assistantId = row.id
      items.value.push(row)
    }
    return items.value.find((i) => i.id === assistantId)
  }

  const removeAssistantIfEmpty = () => {
    if (assistantId == null) return
    const row = items.value.find((i) => i.id === assistantId)
    if (!row || row.content.trim()) return
    const idx = items.value.findIndex((i) => i.id === assistantId)
    if (idx >= 0) items.value.splice(idx, 1)
    assistantId = null
  }

  let latestSuggested = ''
  let ended = false
  let acc = ''

  const finishStream = () => {
    if (ended || gen !== activeStreamGen) return
    ended = true
    if (assistantId != null) {
      const row = items.value.find((i) => i.id === assistantId)
      if (row) row.streaming = false
    }
    loading.value = false
    streaming.value = false
    scrollBottom()
  }

  abortStream.value = streamChat(
    {
      message,
      chatId: chatId.value,
    },
    {
      onChunk: (chunk) => {
        acc += chunk
        const a = ensureAssistant()
        if (!a) return
        if (isTicketOfferText(acc) || isTicketOfferPrefix(acc)) {
          a.content = ''
        } else {
          a.content = acc
        }
        scrollBottom()
      },
      onSources: (payload) => {
        const a = ensureAssistant()
        if (a) a.sources = payload.sources || []
        finishStream()
      },
      onTicketOffer: (payload) => {
        latestSuggested = payload.suggestedDescription || ''
        removeAssistantIfEmpty()
        items.value.push({
          id: ++idSeq,
          type: 'ticket_entry',
          timestamp: new Date().toISOString(),
          suggestedDescription: latestSuggested,
          content: '抱歉，我暂时给不了更合适的指引。如果需要人工协助，提交反馈后客服会继续跟进。',
        })
        pendingSuggested.value = latestSuggested
        finishStream()
      },
      onDone: () => {
        if (isTicketOfferText(acc)) {
          latestSuggested = message
          removeAssistantIfEmpty()
          items.value.push({
            id: ++idSeq,
            type: 'ticket_entry',
            timestamp: new Date().toISOString(),
            suggestedDescription: latestSuggested,
            content: '抱歉，我暂时给不了更合适的指引。如果需要人工协助，提交反馈后客服会继续跟进。',
          })
          pendingSuggested.value = latestSuggested
        }
        finishStream()
      },
      onError: (e) => {
        finishStream()
        showToast(e.message || '请求失败')
      },
    }
  )
}

function stopStream() {
  activeStreamGen += 1
  abortStream.value?.()
  loading.value = false
  streaming.value = false
  for (const item of items.value) {
    if (item.type === 'chat' && item.role === 'assistant' && item.streaming) {
      item.streaming = false
    }
  }
}

function openTicket(suggested) {
  pendingSuggested.value = suggested || pendingSuggested.value
  sheetVisible.value = true
}

function onTicketSuccess(data) {
  showToast(`提交成功：${data.ticketNo}`)
  items.value.push({
    id: ++idSeq,
    type: 'system',
    content: `工单已提交，编号 ${data.ticketNo}`,
  })
  scrollBottom()
}
</script>

<style scoped>
.page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f6f8;
}
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.bottom {
  flex-shrink: 0;
  margin: 0 12px 12px;
  padding: 12px 12px max(12px, env(safe-area-inset-bottom));
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.06);
}
.system {
  text-align: center;
  font-size: 12px;
  color: #888;
  margin: 8px 0;
}
.toast {
  position: fixed;
  left: 50%;
  bottom: 100px;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.78);
  color: #fff;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 13px;
  z-index: 200;
}
</style>
