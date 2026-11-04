import request from './request.js'

export function createTicket(body) {
  return request.post('/help_center/tickets', body)
}
