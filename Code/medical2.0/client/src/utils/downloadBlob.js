/**
 * Blob 下载工具
 * 统一处理后端返回的文件流下载
 */

/**
 * 从 content-disposition 头解析文件名
 * @param {string} disposition - content-disposition 头的值
 * @returns {string|null} 解析后的文件名
 */
export function parseFilename(disposition) {
  if (!disposition) return null
  // 优先尝试 filename*=UTF-8'' 格式（RFC 5987）
  const utf8Match = disposition.match(/filename\*=UTF-8''(.+)/i)
  if (utf8Match) {
    return decodeURIComponent(utf8Match[1])
  }
  // 回退到 filename="xxx" 或 filename=xxx 格式
  const match = disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/i)
  if (match) {
    let filename = match[1].replace(/['"]/g, '')
    try {
      filename = decodeURI(filename)
    } catch (e) {
      // 解码失败则使用原值
    }
    return filename
  }
  return null
}

/**
 * 下载 blob 响应
 * @param {Object} response - axios 响应对象（responseType: 'blob'）
 * @param {string} [defaultFilename] - 默认文件名（当 headers 中无文件名时使用）
 */
export function downloadBlob(response, defaultFilename) {
  const headers = response.headers
  // content-disposition 可能在不同位置
  const disposition = headers['content-disposition'] || headers
  const filename = parseFilename(typeof disposition === 'string' ? disposition : '') || defaultFilename || '下载文件'

  const blob = new Blob([response.data], { type: 'application/octet-stream' })
  const url = window.URL.createObjectURL(blob)

  const link = document.createElement('a')
  link.style.display = 'none'
  link.href = url
  link.setAttribute('download', filename)
  document.body.appendChild(link)
  link.click()

  // 清理，防止内存泄漏
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}
