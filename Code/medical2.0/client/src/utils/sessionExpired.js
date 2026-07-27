/**
 * 会话过期统一处理（request.js / requestReport.js 共用）
 * 解决两个问题：
 * 1. 页面并发多个请求同时过期时，每个请求都弹一次"登录已过期"提示 → 提示走全局时间窗去重，只弹一次
 * 2. 并发触发多次 router.replace('/login') → NavigationDuplicated 未捕获异常，跳转做去重并吞掉导航异常
 */
import router from '@/router'

// 提示去重：时间窗口内（默认3秒）只弹一次
let lastMessageTime = 0
const MESSAGE_DEDUPE_WINDOW = 3000

// 跳转去重：跳转进行中不再重复触发
let isRedirecting = false

export function handleSessionExpired() {
    const now = Date.now()
    if (now - lastMessageTime > MESSAGE_DEDUPE_WINDOW) {
        lastMessageTime = now
        ELEMENT.Message({
            message: '登录已过期，请重新登录',
            type: 'warning',
            duration: 2000
        })
    }

    // 已在登录页则不重复跳转
    if (router.currentRoute.path === '/login') return
    if (isRedirecting) return
    isRedirecting = true
    router
        .replace('/login')
        .catch(() => {}) // 忽略 NavigationDuplicated 等导航异常
        .finally(() => {
            isRedirecting = false
        })
}
