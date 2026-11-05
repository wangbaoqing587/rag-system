<template>
  <div ref="root" class="app-select" :class="{ open, disabled }">
    <button
      type="button"
      class="trigger"
      :disabled="disabled"
      :aria-expanded="open"
      @click="toggle"
    >
      <span class="value">{{ currentLabel }}</span>
      <svg class="chev" viewBox="0 0 20 20" fill="none" aria-hidden="true">
        <path
          d="M5 7.5L10 12.5L15 7.5"
          stroke="currentColor"
          stroke-width="1.5"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
      </svg>
    </button>
    <Transition name="drop">
      <ul v-if="open" class="menu" role="listbox">
        <li
          v-for="opt in options"
          :key="String(opt.value)"
          role="option"
          :aria-selected="modelValue === opt.value"
          :class="['item', { active: modelValue === opt.value }]"
          @click="pick(opt.value)"
        >
          <svg
            v-if="modelValue === opt.value"
            class="check"
            viewBox="0 0 16 16"
            fill="none"
            aria-hidden="true"
          >
            <path
              d="M3.5 8.5L6.5 11.5L12.5 4.5"
              stroke="currentColor"
              stroke-width="1.5"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
          <span>{{ opt.label }}</span>
        </li>
      </ul>
    </Transition>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  options: { type: Array, default: () => [] },
  placeholder: { type: String, default: '请选择' },
  disabled: { type: Boolean, default: false },
})

const emit = defineEmits(['update:modelValue', 'change'])

const root = ref(null)
const open = ref(false)

const currentLabel = computed(() => {
  const hit = props.options.find((o) => o.value === props.modelValue)
  return hit?.label ?? props.placeholder
})

function toggle() {
  if (props.disabled) return
  open.value = !open.value
}

function pick(value) {
  if (value !== props.modelValue) {
    emit('update:modelValue', value)
    emit('change', value)
  }
  open.value = false
}

function onDocClick(e) {
  if (!root.value?.contains(e.target)) {
    open.value = false
  }
}

onMounted(() => document.addEventListener('click', onDocClick))
onBeforeUnmount(() => document.removeEventListener('click', onDocClick))
</script>

<style scoped>
.app-select {
  position: relative;
  width: 100%;
}

.trigger {
  width: 100%;
  height: var(--control-height);
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 0 12px;
  border: 1px solid var(--border-strong);
  border-radius: var(--radius-sm);
  background: var(--surface);
  color: var(--text);
  line-height: 1;
  cursor: pointer;
  text-align: left;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.trigger:hover:not(:disabled) {
  border-color: #cbd5e1;
}

.app-select.open .trigger,
.trigger:focus-visible {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-soft);
}

.trigger:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.value {
  flex: 1;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chev {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: var(--text-muted);
  transition: transform 0.15s ease;
}

.app-select.open .chev {
  transform: rotate(180deg);
  color: var(--primary);
}

.menu {
  position: absolute;
  z-index: 50;
  top: calc(100% + 6px);
  left: 0;
  right: 0;
  margin: 0;
  padding: 6px;
  list-style: none;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.12);
}

.item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 10px;
  border-radius: 4px;
  font-size: 14px;
  color: var(--text);
  cursor: pointer;
  transition: background 0.12s ease, color 0.12s ease;
}

.item:hover {
  background: #f1f5f9;
}

.item.active {
  background: var(--primary-soft);
  color: var(--primary);
  font-weight: 500;
}

.check {
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.drop-enter-active,
.drop-leave-active {
  transition: opacity 0.12s ease, transform 0.12s ease;
}

.drop-enter-from,
.drop-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (prefers-reduced-motion: reduce) {
  .drop-enter-active,
  .drop-leave-active {
    transition: none;
  }
}
</style>
