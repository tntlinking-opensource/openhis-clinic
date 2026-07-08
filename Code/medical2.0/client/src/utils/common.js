
/**
* 两层循环实现建树 把list数据通过 id，和parentId够造成一个树
*/
export function buildTree(list) {
  const map = {}
  const trees = []
  list.forEach(item => {
    map[item.id] = { ...item, children: [] }
  })
  list.forEach(item => {
    if (item.parentId === '0' || !item.parentId) {
      trees.push(map[item.id])
    } else if (map[item.parentId]) {
      map[item.parentId].children.push(map[item.id])
    }
  })
  return trees
}

// 保留旧名称以保持向后兼容
export const bulidTree = buildTree

export function isLightOrDark(rgbColor) {
  let currentRgb = rgbColor.replace("rgb(", "").replace(")", "");
  let currentRgbArr = currentRgb.split(',');
  let grayLevel =  currentRgbArr[0] * 0.299 + currentRgbArr[1] * 0.587 + currentRgbArr[2] * 0.114;
  return grayLevel >= 192
}

/**
 * 获取 N 天前的日期
 * @param {number} days 天数
 * @returns {Date} N 天前的 Date 对象
 */
export function daysAgo(days) {
  return new Date(Date.now() - days * 86400000)
}

/**
 * 格式化日期为 YYYY-MM-DD HH:mm:ss 字符串
 * @param {Date} date 日期对象
 * @returns {string} 格式化后的日期字符串
 */
export function formatDateTime(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}:${s}`
}

/**
 * BigNumber 格式化工具（千分位，保留2位小数）
 * 需要项目中已引入 BigNumber.js
 * @param {BigNumber} BigNumber BigNumber 构造函数
 * @returns {function} 格式化函数
 */
export function createBigNumFormatter(BigNumber) {
  return function bigNum(num) {
    if (num || num === '0') {
      return new BigNumber(num).toFormat(2)
    }
    return ''
  }
}
