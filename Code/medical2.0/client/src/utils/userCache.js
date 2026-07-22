/**
 * 用户信息缓存工具
 * 避免重复解析sessionStorage中的JSON
 */

let currentUserCache = null
let currentCompanyCache = null

/**
 * 获取当前用户信息（带缓存）
 * @returns {Object} 用户信息
 */
export function getCurrentUser() {
    if (!currentUserCache) {
        const userStr = sessionStorage.getItem('currentUser')
        if (userStr) {
            try {
                currentUserCache = JSON.parse(userStr)
            } catch (e) {
                console.error('解析用户信息失败:', e)
                currentUserCache = {}
            }
        }
    }
    return currentUserCache || {}
}

/**
 * 获取当前公司信息（带缓存）
 * @returns {Object} 公司信息
 */
export function getCurrentCompany() {
    if (!currentCompanyCache) {
        const companyStr = sessionStorage.getItem('currentCompany')
        if (companyStr) {
            try {
                currentCompanyCache = JSON.parse(companyStr)
            } catch (e) {
                console.error('解析公司信息失败:', e)
                currentCompanyCache = {}
            }
        }
    }
    return currentCompanyCache || {}
}

/**
 * 获取当前用户ID
 * @returns {string} 用户ID
 */
export function getCurrentUserId() {
    return getCurrentUser().id || ''
}

/**
 * 获取当前公司ID
 * @returns {string} 公司ID
 */
export function getCurrentCompanyId() {
    return getCurrentCompany().id || ''
}

