/**
 * Standalone date formatting function.
 * @param {Date} date - The date to format
 * @param {string} fmt - Format string (e.g. "yyyy-MM-dd HH:mm:ss")
 * @returns {string} The formatted date string
 */
export function formatDate(date, fmt) {
  if (!(date instanceof Date)) {
    date = new Date(date);
  }
  const o = {
    "M+": date.getMonth() + 1,
    "d+": date.getDate(),
    "H+": date.getHours(),
    "m+": date.getMinutes(),
    "s+": date.getSeconds(),
    "S+": date.getMilliseconds()
  };
  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
  }
  for (const k in o) {
    if (new RegExp("(" + k + ")").test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(String(o[k]).length)));
    }
  }
  return fmt;
}
