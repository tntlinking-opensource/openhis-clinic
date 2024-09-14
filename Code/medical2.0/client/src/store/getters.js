const getters = {
    showLoading: state => state.showLoading,
    breadcrumbItems: state => state.breadcrumbItems,
    theme: state => state.settings.theme,
    settings: state => state.settings,
    sys: state => state.sys,
    menus: state => state.menus,
    topMenuId: state => state.menus.topMenuId,
    toPath: state => state.menus.toPath,             //登录前请求的页面路径
    noticeTotal: state => state.notices.noticeTotal, //未读消息总数
    // geoMap: state => state['report/map'].geoMap // 获取地图数据
};

export default getters
