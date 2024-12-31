// import Vue from 'vue'
// import Router from 'vue-router'
import LoginView from '@/views/login/index'
import { getLocalToken, getLocalRouters } from '@/utils/auth'
import { routerTree, handleFamily } from '@/utils/routerTree'
import charge from '@/views/charge/index'
import chargeaccountreport from '@/views/chargeaccountreport/index'
import chargecombinereport from '@/views/chargecombinereport/index'


const _import = require('./_import_' + process.env.NODE_ENV)
Vue.use(VueRouter)

//按需加载
/**
 * 有时候我们想把某个路由下的所有组件都打包在同个异步 chunk 中。
 * 只需要 给 chunk 命名，提供 require.ensure 第三个参数作为 chunk 的名称。
 * Webpack 将相同 chunk 下的所有异步模块打包到一个异步块里面，
 * 这也意味着我们无须明确列出 require.ensure 的依赖（传空数组就行）。
 */
//const Index = resolve => require(['@/views/index'], resolve)
//const foodList = r => require.ensure([], () => r(require('@/page/foodList')), 'foodList');

const router = new VueRouter({
  routes: [
    {
      path: '/login',
      name: "login",
      component: LoginView,
      meta: { requiresAuth: false }
    },{
      path: '/',
      component: resolve => require(['@/views/home/index'], resolve),
      meta: { requiresAuth: true }
    },
    {
      path: '/preview/:reportId',
      component: _import('report/components/PreviewEject'),
      hidden: true
    },
    {
      path:'/charge',
      name:'charge',
      component:charge
    },{
      path:'/chargeaccountreport',
      name:'charchargeaccountreportge',
      component:chargeaccountreport
    },{
      path:'/chargecombinereport',
      name:'chargecombinereport',
      component:chargecombinereport
    }
  ]
});

generateIndexRouter();

// 验证token，存在才跳转
router.beforeEach((to, from, next) => {
  let token = getLocalToken()
  /*
	if(to.meta.requiresAuth) {    //判断是否需要登录权限
		next()
	}
  */

  if(to.path === '/login'){
    next()
  }else{
    if(token){
      next()
    }else{
      if(to.path === '/'){
        next('/login')
      }else{
        router.app.$store.dispatch('menus/SetToPath',to.path );
        ELEMENT.Message({message:"您还没有登录，请先登录",type: 'warning', duration: 2 * 1000});
        next('/login')
      }
    }
  }


});

router.afterEach((to, from) => {
  // 设置面包屑
  let breadCrumbItems = []
  /*  let homePageCrumb = {
	  title: '首页',
	  to: '/'
	}
	breadCrumbItems.push(homePageCrumb)*/
  if(to.meta.nameFullPath) {
    let fullPathSplit = to.meta.nameFullPath.split('/')
    fullPathSplit.forEach(( item, index ) => {
      let routerBreadCrumb = {
        title: item,
        to: (index == fullPathSplit.length - 1 ? to.path : '')
      }
      breadCrumbItems.push(routerBreadCrumb)
    });
  }
  // 更新到state

  if (router.app.$store) {
    router.app.$store.dispatch('setBreadcurmbItems', breadCrumbItems)
  }
})

// 生成首页路由
function generateIndexRouter() {
  if (!sessionStorage.routers) {
    return
  }
  let indexRouter = {
    path: '/',
    name: "/",
    component: _import('home/index'),
    children: [
      ...generateChildRouters()
    ]
  }
  // router.addRoutes([indexRouter])
  router.addRoute('/', indexRouter)
}

// 生成嵌套路由（子路由）
function generateChildRouters() {
  let routers = getLocalRouters()
  let routerTreeArr = routerTree(routers)
  let childRouters = []
  for(let router of routers) {
    if(router.code) {
      let routerProps = JSON.parse(router.properties)
      let childRouter = {
        path: router.url,
        name: router.code,
        component: _import(router.code + '/index'),
        meta: {
          name: router.name,
          cssClass: routerProps.cssClass,
          routerId: router.id,
          parentId: router.parent.id,
          family: handleFamily(routerTreeArr, item => item.id === router.id),
          requiresAuth: routerProps.meta.requiresAuth,
          nameFullPath: routerProps.nameFullPath,
          linkUrl: routerProps.linkUrl
        }
      };
      childRouters.push(childRouter)
    }
  }
  let redirectRouter = {
    path: 'redirect',
    component: _import('redirect/index')
  }
  childRouters.push(redirectRouter)


  return childRouters
}

// 避免重复路由
const originalPush = VueRouter.prototype.push

VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

export default router;
