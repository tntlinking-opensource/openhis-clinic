# Geeke-Client

------

Geeke-Client是基于vue+element开发的Geeke项目的前端应用，包含了如下功能特点：

> * Vue+Element，轻巧、高性能、易于上手。
> * 后台Web应用无状态，使用JWT管理身份认证。
> * 动态路由，按需加载。


## 使用方法

### 修改配置文件

config\dev.env.js和prod.env.js，修改BASE_API为 【back】服务 的地址。

### 运行

 - 安装依赖包：npm（建议使用cnpm） install
 - npm run dev，通过http://localhost:3000 访问
 - npm run build，完成后打开dist\index.html访问
 - 默认用户：system，口令：123456

### 注意事项

**如需添加模块，则需要：**

 1. 在路由管理中定义模块内容，路由代码表示前端源码路径中views下的文件夹名称，如添加商品管理，路由代码设置为product，则需要在views文件夹下创建product文件夹，然后创建index.vue；扩展属性中的requiresAuth表示访问该路由是需要在前端先验证token是否存在，cssClass为首页左侧路由菜单的图标样式（element-ui的Icon图标样式），nameFullPath为导航面包屑的完整路径。
 2. 在资源管理中定义模块的功能，资源的url需要和Web服务Controller中定义的API Url一致，资源权限中冒号后边的内容和Http Method的对应关系：get->read、put->update、post->create、delete->delete
 