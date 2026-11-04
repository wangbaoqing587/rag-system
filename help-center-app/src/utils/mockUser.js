const USER_KEY = 'help_center_user'
const CHAT_ID_KEY = 'help_center_chat_id'

export function loadUser() {
  try {
    const raw = sessionStorage.getItem(USER_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function saveUser(user) {
  sessionStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearUser() {
  sessionStorage.removeItem(USER_KEY)
  sessionStorage.removeItem(CHAT_ID_KEY)
}

export function getOrCreateChatId(regenerate = false) {
  if (regenerate) {
    const id = crypto.randomUUID()
    sessionStorage.setItem(CHAT_ID_KEY, id)
    return id
  }
  let id = sessionStorage.getItem(CHAT_ID_KEY)
  if (!id) {
    id = crypto.randomUUID()
    sessionStorage.setItem(CHAT_ID_KEY, id)
  }
  return id
}

export function formatUserId(digits) {
  return `AF-${digits}`
}

export function validateDigits10(value) {
  return /^\d{10}$/.test(value)
}

export function validateUserName(name) {
  return name && [...name].length <= 12
}
