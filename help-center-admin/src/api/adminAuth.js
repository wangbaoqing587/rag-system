import request from './request.js'

export function login(username, password) {
  return request.post('/help_center/admin/login', { username, password })
}
