/**
 * 数据解析工具函数
 * 集中管理脆弱的数据格式解析逻辑
 */

/**
 * 从复合值中提取数值部分
 * 例如: "BID_2" → 2, "tollType_0" → "0"
 * @param {string} value - 复合值字符串 (如 "BID_2")
 * @param {string} separator - 分隔符 (默认 "_")
 * @param {*} defaultValue - 解析失败时的默认值
 * @returns {string} 分隔符后的部分
 */
export function extractSuffix(value, separator = '_', defaultValue = '') {
  if (!value || typeof value !== 'string') return defaultValue;
  const parts = value.split(separator);
  return parts.length > 1 ? parts[parts.length - 1] : defaultValue;
}

/**
 * 从复合值中提取前缀部分
 * 例如: "BID_2" → "BID", "tollType_0" → "tollType"
 * @param {string} value - 复合值字符串
 * @param {string} separator - 分隔符 (默认 "_")
 * @param {*} defaultValue - 解析失败时的默认值
 * @returns {string} 分隔符前的部分
 */
export function extractPrefix(value, separator = '_', defaultValue = '') {
  if (!value || typeof value !== 'string') return defaultValue;
  const parts = value.split(separator);
  return parts.length > 1 ? parts[0] : defaultValue;
}

/**
 * 从频次值中提取次数
 * 例如: "BID_2" → 2, "QD_1" → 1
 * @param {string} frequencyValue - 频次值
 * @returns {number} 频次次数
 */
export function getFrequencyCount(frequencyValue) {
  const suffix = extractSuffix(frequencyValue, '_', '1');
  const num = parseInt(suffix, 10);
  return isNaN(num) ? 1 : num;
}

/**
 * 拼接药品规格字符串
 * 例如: "10mg*12片/盒"
 * @param {object} drug - 药品对象
 * @returns {string} 规格字符串
 */
export function formatDrugSpec(drug) {
  if (!drug) return '';
  const dosis = drug.dosis || '';
  const dosisUnit = drug.dosisUnit ? (typeof drug.dosisUnit === 'object' ? drug.dosisUnit.name : drug.dosisUnit) : '';
  const preparation = drug.preparation || '';
  const preparationUnit = drug.preparationUnit ? (typeof drug.preparationUnit === 'object' ? drug.preparationUnit.name : drug.preparationUnit) : '';
  const pack = drug.pack ? (typeof drug.pack === 'object' ? drug.pack.name : drug.pack) : '';
  return `${dosis}${dosisUnit}*${preparation}${preparationUnit}/${pack}`;
}
