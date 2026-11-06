import request from './request.js'

export function fetchTickets(params) {
  return request.get('/help_center/admin/tickets', { params })
}

export function fetchTicket(id) {
  return request.get(`/help_center/admin/tickets/${id}`)
}

export function patchTicketStatus(id, body) {
  return request.patch(`/help_center/admin/tickets/${id}/status`, body)
}

export function patchTicketInternalNote(id, body) {
  return request.patch(`/help_center/admin/tickets/${id}/internal-note`, body)
}

export function patchTicketReply(id, body) {
  return request.patch(`/help_center/admin/tickets/${id}/reply`, body)
}
