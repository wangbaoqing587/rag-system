import request from './request.js'

export function chatSync(params) {
  return request.get('/ai/help_center/chat/sync', { params })
}

export function streamChat(params, handlers) {
  const qs = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v != null && v !== '') qs.set(k, v)
  })
  const url = `/api/ai/help_center/chat/sse?${qs.toString()}`
  let closed = false
  let doneCalled = false

  const finish = () => {
    if (doneCalled) return
    doneCalled = true
    handlers.onDone?.()
  }

  const es = new EventSource(url)

  es.onmessage = (ev) => {
    if (ev.data != null && ev.data !== '') {
      handlers.onChunk?.(ev.data)
    }
  }

  es.addEventListener('sources', (ev) => {
    try {
      let payload = JSON.parse(ev.data)
      if (typeof payload === 'string') payload = JSON.parse(payload)
      handlers.onSources?.(payload)
    } catch {
      handlers.onSources?.({ sources: [] })
    }
  })

  es.addEventListener('ticket_offer', (ev) => {
    try {
      handlers.onTicketOffer?.(JSON.parse(ev.data))
    } catch {
      /* ignore malformed ticket_offer */
    }
  })

  es.onerror = () => {
    if (closed) return
    closed = true
    es.close()
    finish()
  }

  es.onopen = () => {
    /* connection established */
  }

  return () => {
    if (closed) return
    closed = true
    es.close()
    finish()
  }
}
