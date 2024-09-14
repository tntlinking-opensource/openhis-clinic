import { getLocalRouters } from '@/utils/auth'
const state = {
  topMenuId: '',
  menus: [],
  toPath:''
}


const actions = {
  getMenus({ commit }, menuId) {
    let menus = [];
    let routers = getLocalRouters()
    for (let router of routers) {
      let routerProperties = JSON.parse(router.properties)
      let item = {
        id: router.id,
        code: router.code,
        name: router.name,
        url: hasContainSpecial(router.url)? `/${router.url.split(':')[0]}${routerProperties.params}` : `/${router.url}`,
        level: router.level,
        parentId: router.parent && router.parent.id ? router.parent.id: '',
        nameFullPath: routerProperties.nameFullPath,
        cssClass: routerProperties.cssClass,
        component: routerProperties.component,
        children:[],
      };
      // if (router.parent.id === menuId || (router.parent.id == 0 && router.code)) {
      if (router.parent.id === menuId || (router.parent.id == 0 && router.code && router.code != 'homepage/homepage')) {
          menus.push(tree(item, routers))
      }
    }
    commit('GET_MENUS', { menus, menuId })
  },
  SetToPath({ commit }, data) {
    commit('Set_ToPath', data)
  },

};
const hasContainSpecial = function (str) {
  const specialStr = new RegExp(/:/g)
  return specialStr.test(str)
}
const tree = function (menu, routers){
  for (let router of routers) {
    let routerProperties = JSON.parse(router.properties);
    let item = {
      id: router.id,
      code: router.code,
      name: router.name,
      url: hasContainSpecial(router.url)? `/${router.url.split(':')[0]}${routerProperties.params}` : `/${router.url}`,
      level: router.level,
      parentId: router.parent && router.parent.id ? router.parent.id: '',
      nameFullPath: routerProperties.nameFullPath,
      cssClass: routerProperties.cssClass,
      component: routerProperties.component,
      children:[],
    };
    if (menu.id === item.parentId && menu.parentId != "0"){
      menu.children.push(tree(item, routers))
    }
  }
  return menu;
};

const mutations = {
  GET_MENUS(state, data) {
    state.menus = data.menus
    state.topMenuId = data.menuId
  },
  Set_ToPath(state, data){  //缓存登录前访问路径
    state.toPath = data;
  },
}

export default {
  namespaced: true,
  state,
  actions,
  mutations
}
