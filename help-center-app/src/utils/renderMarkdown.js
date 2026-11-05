import { marked } from 'marked'

marked.setOptions({
  gfm: true,
  breaks: true,
})

const renderer = new marked.Renderer()
renderer.html = () => ''

marked.use({ renderer })

export function renderMarkdown(text) {
  if (text == null || text === '') return ''
  const normalized = String(text).replace(/\n+$/, '')
  let html = marked.parse(normalized, { async: false })
  html = html.replace(/(<br\s*\/?>\s*)+(\s*<\/p>)/gi, '$2')
  html = html.replace(/<p>\s*<\/p>/gi, '')
  return html
}
