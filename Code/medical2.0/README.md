
# 代码生成快速开发平台应用
操作步骤

1） 项目搭建，搭建geeke项目（1.1节）

2） 使用ERMaster建立数据模型（1.2节）

3） 进入代码生成模块，添加业务表配置（1.3节）

4） 进入代码生成模块，添加方案配置并生成代码（1.4节）


## 1.1项目搭建
下载代码： https://code.newtouch.com/BfENepkG/geeke.git

##### 数据库初始化
通过back\db\geedb.sql生成mysql的schema。

##### eclipse后台工程
1） 导入maven项目，
 
![导入maven功能](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/34fea13e44d449a5bccbd8e0a80b787c)

maven 的pom文件：\back\back

2） 修改数据库配置

修改文件back\src\main\resources\application.yml，数据库配置。

spring:

  datasource:

    driver-class-name: com.mysql.cj.jdbc.Driver

    url: jdbc:mysql://10.1.1.219:3306/geedb?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC

    username: geeke

    password: 123

3) 修改代码生成配置

genConfigure:

  rootPath: E:\git\geeke  		 #代码生成地址配置

  dbName: mysql                          #数据库类型:  mysql/oracle/mssql

4）启动后台服务
 
![导入maven功能](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/c1cd93710fe34fa6ac2791627026b773)

Springboot 项目启动时报SilentExitException异常，进入断点，F8之后程序能正常跑，起初我以为只是个别情况，后来发现每次我在Eclipse IDE（Spring Tool Suite）中的调试模式下运行Spring Boot项目时，线程都会停止在“throw new SilentExitException（）;” 即使没有断点也会进入。

查阅了资料，是spring-boot-devtools模块的一个已知问题（参阅https://github.com/spring-projects/spring-boot/issues/3100），因为用的是Eclipse，网上也给出了一些解决方案，可以在Eclipse中首选项中Java - > Debug首选项中切换“暂停执行未捕获的异常”复选框以防止它发生。

##### 前端工程
1） 在cmd窗口中切换到代码\client目录

按下面的顺序输入命令

npm install

npm run dev

用户账号和密码：system/123456

## 1.2数据模型

1） 进行数据库设计，建表。

2） 自己建立表模型是请拷贝一份“geek.erm”文件，在此模型基础上建立自己的业务表即可。

3） eclipse安装ERMaster插件

下载插件，地址：https://sourceforge.net/projects/ermaster/files/

解压org.insightech.er_1.0.0.v20150619-0219.zip到eclipse下的plugins目录中，重启eclipse。

![安装ERMaster插件](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/a33c93f3d00f4b95a6f1ad8a7bc11e63)

### 1.2.1 业务表必须包含的字段
红框内为必须有的字段：

![业务须有字段](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/5b2630dbf09a450bb73d0dcb0d7d25c6)

### 1.2.2 一对多必须包含的字段
红框内为必须有的字段：

![1对多须有字段](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/1c4dbed81a34460eb9e16fa5204e8754)

### 1.2.3 树结构必须包含的字段
红框内为必须有的字段：

![树结构须有字段](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/0982cd5765f347ef9ee1e921efe91ff5)

### 1.2.4 快速添加必须包含的字段
在geek.erm文件中已内置两个字段组，分别是common和tree_field，在表编辑界面中可快速选择并添加相应字段，如下图：

![快速添加必须包含的字段](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/3dc6a9659b124a7e9a87c52505848af5)


## 1.3业务表配置

![业务表配置](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/be7ff7d687924949b4961adc0df38947)

##### 基本信息
1） 名称（表名）：物理表表名

2） 说明：物理表表描述

3） 实体类名称：生成表关联的实体类名称

4） 关联父表：关联父表的表名，外键：当前表关联父表的主键，如果当前表为子表，需在此指定父表及外键。外键字段需在字段列表中手动设置属性名（对象.主键，例如：将userId修改为user.id）

5） 父表外键：与关联父表关联的字段

6） 是否关联表：指明业务表示否属于两个业务实体表多对多情况下的关联表。

7） 筛选条件：配置列表查询时的数据where条件。

##### 约束字段表
支持数据重复性约束检查，如用户登陆名不能重复。

1） 名称：约束名称

2） 描述：保存重复数据时的提示信息

3） 约束字段：约束检查的数据库字段


##### 业务字段
1） 列名：数据表定义的字段名称

2） 说明：数据表定义的字段注释

3） 物理类型：数据表定义字段类型

4） Java类型：实体对象的属性字段类型

5） 自定义类型：当java类型型选择customer时，需指定的具体业务定义的类型（定义的方案）

6） 属性名称：实体对象的属性字段（对象名.属性名|属性名2|属性名3，例如：用户user.id|name|loginName，属性名2和属性名3为Join时关联查询的字段）

7） 默认值：初始化对象默认值。可以使具体的值，如1或'2018年';可以是js的函数，如new Data(）; 可以js的自定义匿名函数，如：

function(） {var user = JSON.parse(sessionStorage.getItem('currentUser'））; return {id: user.objectId, name:''};}(）

8） 主键：是否是主键字段

9） 可空：该字段是否可为空，如果不为空则会生成界面不能为空的验证

10） 可插：是否是插入字段，如果是则包含在insert语句里

11） 可编：是否是编辑字段，如果是则包含在update语句里，并且在编辑界面中根据【控件】生成相应的录入框

12） 列显：是否显示在默认的列表中，如果是则包含在列表页的表格列里

13） 列宽：显示在主页面列表上的最小宽度（min-width）

14） 查询：是否是查询字段，如果是则包含在查询栏中作为查询条件。

15） 查询方式：查询字段的查询方式，也就是where后的条件表达式，如：字段1=字段2AND字段3>字段4AND字段5 !=字段6。

16） 控件：表单中字段生成的组件，如：input，select，treeselect，areatext等等
17)  联动变量：当联动变量的值发生变化时，主界面查询条件选择框或编辑选择框的选项重新初始化。
    使用场景如：切换公司，或部门以后，触发员工选择框的选项发生变化。
    配置：员工字段配置公司和部门为联动变量。代码生成工具将自动生成watch，监听公司和部门变量，并根据联动参数更新员工列表数据。

18） 联动参数：主界面查询条件选择框或编辑选择框中，选项的过滤条件。如：

	'a.company_id': {queryType: '=', value: This.company.id}

    [1] 条件的格式为：字段: {条件运算符，条件值}

    [2] 多个条件的情况下，采用逗号分隔开。

    [3] This是保留字，代表UI界面的model业务对象。

    [4] 过滤条件还可以使用js变量、js代码，甚至js匿名函数。示例：

'a.building_id': {queryType: '=', value: function(） {var user = JSON.parse(sessionStorage.getItem('currentUser'））; return user.objectId;}(）}

19） 排序：字段生成的先后顺序，升序。决定列表中默认顺序和编辑框中的控件顺序。



## 1.4方案配置
![方案配置](https://console-step.newtouch.com/stepapi/v3/project/6f345ba01729459fb9a44651af59866e/file/download/db1465df9dc64c6599f29ab42b5853f0)

1） 方案名称：自定的方案名称。

2） 模板分类：生成的模板，目前支持以下模板。

    [1] 增删改查（单表）：如角色管理

    [2] 增删改查（一对多）：如字典管理

    [3] 树结构表（一体）: 如路由管理

    [4] 左树右表：如资源管理

    [5] 父树-子树表：（如业务模型公司（树形），部门（树形），对部门的管理）

    [6] 仅持久层（dao/entity/mapper）： 自动生成dao，entity和mapper代码

    [7] 仅服务和接口（api/controller/service）： 自动生成api，controller和service代码。

    [8]） 持久层、服务和接口（dao/api）：自动包含仅持久层（dao/entity/mapper）和仅服务和接口（api/controller/service）的代码。

3） 包路径：生成哪个包下。

4） 模块名：生成包下的模块名称，模块名称下进行分层。

5） 子模块名：分层下的文件夹，可为空。

6） 功能名：生成到类注释里。

7） 功能名简写：生成功能提示，如TAB上、列表上、提示信息等。如：保存"某某"成功

8） 作者：开发者姓名

9） 编辑宽度：编辑框的宽度，如：640px，或者80%

10） 编辑列数： 编辑框的输入框，按照几列布局。

11） 业务表：方案对应的业务表。一对多情况下选择主表，左树右表情况下选择子表。

注：

    [1] 后台代码生成结构为 {包路径}/{模块名}/controller/{子模块名}/{类名}

    [2] 前端代码生成结构为src/views/{模块名}/{子模块名}/{类名}







