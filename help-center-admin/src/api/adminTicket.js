import request from './request.js'

export function fetchTickets(params) {
  return request.get('/help_center/admin/tickets', { params })
}

export function fetchTicket(id) {
  return request.get(`/help_center/admin/tickets/${id}`)
}

export function patchTicket(id, body) {
  return request.patch(`/help_center/admin/tickets/${id}`, body)
}
