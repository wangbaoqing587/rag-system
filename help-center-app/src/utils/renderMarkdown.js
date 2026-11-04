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
  return marked.parse(text, { async: false })
}
