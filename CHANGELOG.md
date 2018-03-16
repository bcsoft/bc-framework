# [BC 平台](https://github.com/bcsoft/bc-framework)变更日志

## release v3.2.10 (2018-03-16)
- bc-spider : 设置通过 HttpClientFactory 创建的 HttpClient 实例默认不允许 post/delete 的自动重定向
- bc-spider : 默认支持的重定向次数改为 2
- bc-spider : 增加重定向次数的自定义设置

## release v3.2.9 (2018-03-14)
- bc-spider : 添加 httpOptions 特殊参数支持
- bc-spider : 重构 CaptchaImageCallable  验证码图片获取类
- bc-spider : Set HttpClient max redirect to 1 by default

## release v3.2.8 (2018-03-12)
- Upgrade to httpclient-4.5.5 and httpcore-4.4.9
- 设置通过 HttpClientFactory 创建的 HttpClient 实例默认允许 post/delete 的自动重定向
- 允许通过环境变量 BC_PROXY_PORT、BC_PROXY_HOST 设置 httpclient 的代理

## release v3.2.7 (2018-01-10)
- 发件箱视图支持搜索收件人

## release v3.2.6 (2018-01-03)
- bc-core     : 指定 StringUtils 中 getContentFromClassResource 方法返回 UTF-8 编码格式的字符串
- bc-desktop  : 用户登录后，被禁用的资源信息排除不显示
- bc-identity : 资源视图增加状态列

## release v3.2.5 (2016-12-01)
- BaseCallable 增加对使用指定编码解析响应体的支持
- 增加获取局域网 IP-MAC 地址对应关系的处理脚本

## release v3.2.4 (2016-11-21)
- 修正新高级搜索组件解析 JsonArray 的错误添加单元测试
- 增加范围条件的支持
- StringUtils的值转换增加对 localDate、localDateTime、localTime 的支持

## release v3.2.3 (2016-10-27)
- 增加对新版 vue 高级搜索组件条件数据的封装支持
- StringUtils 增加 money 类型的转换支持
- 修正定时任务模块的错误配置
- 扩展 genson 增加对 java8 日期时间类型的支持

## release v3.2.2 (2016-10-12)
- 修正视图有空列时的导出错误
- upgrade to org.postgresql:postgresql:9.4.1211
- add org.apache.xmlbeans:xmlbeans:2.6.0 to fix error:
	> java.lang.NoSuchMethodError: org.apache.xmlbeans.XmlOptions.setLoadEntityBytesLimit(T)Lorg/apache/xmlbeans/XmlOptions
- 修正 ConditionUtils.toCondition 方法对模糊搜索的处理
- upgrade junit to 4.12, add org.hamcrest:hamcrest-library:1.3
- add org.apache.poi:ooxml-schemas:1.3 to fix poi3.14 error:
	> java.lang.NoClassDefFoundError: org/openxmlformats/schemas/wordprocessingml/x2006/main/impl/CTRImpl$1TList

## release v3.2.1 (2016-09-21)
- bc-parent: poi 升级到 3.14，添加 org.jxls:jxls 2.3.0
- bc-core: StringUtils 增加从类资源文件中获取文本内容并格式化的方法
- bc-web: 添加 WebUtils.encodeFileName 的参数简化版 (不再理会 IE 了)
- bc-db-jdbc: 增加将 0 指转换为 null 的存储函数 zero2null(numeric)
- bc-report: 报表模版扩展为支持通过 spel 来获取报表文件流及使用现有的视图作为报表执行界面

## release v3.2 (2016-08-30)
- bc-parent 重构及增添 bc-framework-bom 来管理版本号
- 迁移前端仓库的文件到此
- 添加子模块 bc-cert、bc-category、bc-express

## release v0.7 (2012-01-?)
### 新特征：
1. 整合activiti流转引擎，实现模块的业务流程处理，流程图的绘制不是一般的酷哦
2. 多页签UI组件的基本封装，终于可以完全使用后台代码生成Tabs了
3. 树UI组件的基本封装
4. Grid增加改变列宽的功能
5. 头像上传、裁剪、缩放功能添加对IE、Opera的支持（现5大浏览器全部支持了）

## release v0.6 (2011-12-11)
### 新特征：
1. 增加Web聊天功能(基于jetty7.5.4+的websocket实现)
2. Grid增加隐藏列处理
3. 增加带下拉菜单的工具条按钮
4. A+B条件的查询实现
5. 增加同步处理模块
6. 增加工作日志模块
7. 自动选择单位、部门、岗位、人员的封装
9. 增加主页右上角聊天图标、邮箱图标的交互设计

### 改进：
1. jQuery升级到1.7
2. EntityAction添加afterCreate、afterOpen、afterEdit、afterSave、beforeSave方法
3. 选择部门信息的封装
4. 附件添加apk类型
5. 添加合并多个条件为Or条件的工具方法
6. 选项条目添加说明字段
7. DateUtils类增加获取月份第一天和最后一天的工具方法
8. 全局事务添加对is*、had*、do*方法的配置

### 修正：
1. 修正必填域验证的bug

## release v0.5 (2011-11-02 纪念世界完全对称日)
### 新特征：
1. 新增定时任务管理模块，可动态增加、暂停、恢复、停止、删除定时任务的调度,查看任务调度日志功能
2. Spring WebService 模块的使用封装，实现java访问.net的WebService，并自动处理DataSet类型的转换
3. 基于spring的JpaTemplate的原生jdbc分页查询封装，视图使用jdbc原生查询提升性能(如组织架构、公告)
4. 登录页面自动据浏览器版本进行升级提示，对IE7、IE6提示系统不支持信息
5. Grid增加更多的格式化处理器，如日期范围格式化、超链接格式化、换行格式化等
6. 列表选择对话框的封装(含分页处理封装)
7. 优化角色权限管理的实现，角色可以直接付给单位、部门、岗位或用户，用户可以继承上级组织的角色
8. 添加登录验证拦截器，只拦截sturts2的请求(/bc)
9. 主页性能优化、用户体验整改：（登录快、使用Cool）  
       顶部导航菜单、底部任务栏、中间为桌面图标，  
       可直接拖动导航菜单到桌面添加桌面图标，  
       可拖动桌面图标到垃圾桶直接删除
10. Actor、ActoryHistory增加pcode、pname属性记录上级的全编码、全名
11. ActorService添加处理隶属多个上级的方法
12. IdGeneratorService添加自动生成流水号功能
13. 工具条添加单选按钮组的后台封装

### 改进：
1. 登录界面美化
2. grid增加显示总条目数的功能
3. 窗口增加控制最大高度的控制参数
4. 增加直接跳转到指定页面的action配置
5. 添加用于测试的长时请求action
6. 未登录状态下载文件的支持
7. 搜索输入框的input控件添加ui-widget-content样式，使其适应主题的变换
8. 增加显示请求信息的测试action：../bc-test/test/showHeader?trace=false
9. 升级POI到3.63版，jxls到1.0RC2版

### 修正：
1. 修正grid的单选功能
2. 修正grid的id列无法执行选择行操作
3. 修正视图中默认的查看按钮的错误
4. 修正资源表单界面类型信息内外部链接反转的错误
5. 修正反馈视图获取数据错误
6. 登录后没有将用户直接拥有的角色添加到上下文的修正

## release v0.4 (2011-8-25)
### 新特征：
1. 页面请求出错提示对话框  
   访问页面出错时，以更友好的界面提示用户；  
   对开发人员可以通过“了解详情”查看详细的错误信息；
2. 增加选项配置模块(配置常用的下拉框选项信息)
3. 增加头像上传、裁剪、缩放功能(如用户表单的头像配置，现仅支持Chrome、Firefox、Safari)
4. 添加Oracle数据库支持(pdm据库设计文件、Oracle11gr2测试)
5. 添加ActorHistory，记录用户隶属组织的变迁历史，为历史数据的正确统计提供支持
6. 初步整理可视化流程编辑器activiti-modeler，部分界面汉化(详见https://github.com/rongjihuang/signavio-core-components)

### 改进：
1. Grid增加记录每个单元格原始值的功能(通过td的data-value属性记录)
2. Grid的刷新方法增加可附加额外参数的功能(通过grid的data-extras属性控制，使用json格式)
3. 表单域验证的配置格式改为可以使用非严格的json格式
4. 条件组件增加别名功能
5. 优化表格列的Formater接口，使其支持注入上下文进行格式化
6. CrudAction改名为EntityAction，并额外封装FileEntityAction，让视图支持非Entity数据类型
7. mysql的id字段类型修改为bigint，统一与oracle主键字段的长度

### 修正：
1. 简化RichEntity、RichFileEntity接口，FileEntity接口添加最后修改人和修改时间信息
2. 修正单元测试：mysql及oracle测试通过  
   运行Mysql单元测试> mvn test -Pmysql -Ddb.ip=127.0.0.1 -Ddb.name=bcdemo  
   运行Oracle单元测试> mvn test -Poracle -Ddb.ip=127.0.0.1 -Ddb.name=orcl
3. 删除基类的inner字段
4. 修正Grid自定义的行双击事件失效的bug
5. 修正整数验证
6. 修正表单域验证的min、max、minLen、maxLen方法
7. 修正json的渲染，避免出现"null"值

## release v0.3 (2011-7-8)
### 新功能：
1. 增加用户反馈模块
2. 增加附件下载记录查看功能
3. 增加主页的主题控制(整合jQueryUI的CSS框架)
4. 增加请求错误时的提示对话框

### 功能加强：
1. 附件管理视图可以在线预览附件、下载单个附件、打包下载选择的附件及查看附件操作日志(删除、下载记录等)
2. 通过附件表单查看该附件的所有操作记录
3. 重构Entity和FileEntity类为Entity、FileEntity、RichEntity、RichFileEntity类
4. 美化jqueryUI的对话框样式，现在看起来应该舒服多了
5. 美化Grid的样式

### 修正：
1. 附件控件上传的附件只能上传到应用目录下
    重构出新的UploadFileServlet，支持将附件上传到app.data.realPath或app.data.subPath；
    附件记录的信息字段path改为只保存相对路径信息，通过联合appPath属性构建绝对路径信息，从而保证附件目录可以随便迁移而不影响现有的数据。
2. 修正附件控件显示该类文档全部附件的bug，各个文档只显示自己的附件了
3. 修正个性化配置保存失效
4. 修正电子公告的查询错误
5. 修正无法上传扩展名为大写的附件

## release v0.2 (2011-6-23)
### 新功能
1. 整合xheditor富文本编辑器，并可自行上传图片、flash、音乐、视频等
2. 新增附件控件
3. 附件控件可跨浏览器批量上传附件  
   * Chrome、Firefox、Safari浏览器使用html5上传文件；  
   * IE浏览器使用SWFUpload组件上传文件；
4. 附件控件可以zip压缩方式打包下载所有上传的附件
5. 在线查看附件文档(如Office文档、图片等)  
        此功能需服务器安装OpenOffice3服务，方法：  
        到http://www.openoffice.org/下载，并安装到服务器，如果是Windows版的服务器操作系统，  
        安装完毕后先行打开openoffice一次并关闭，然后杀掉soffice.bin和soffice.exe两个进程，  
        再打开命令行，进入openoffice安装目录下的program目录，输入如下命令启动openoffice的文档服务：  
   >soffice -headless -accept="socket,port=8100;urp;"
        
        如果附件文档为xls、xlsx、doc、docx格式，系统自动将其转换为pdf格式在线查看，客户端不需要安装Office软件了，
        但需要浏览器支持直接查看pdf文件的功能；
   Chrome原生支持pdf查看，无须插件；Firefox、Safari需安装Adobe Reader插件。
6. 新增电子公告模块：演示富文本编辑器和附件控件的使用
7. 新增附件管理模块：查看已上传到服务器的附件文件信息

## release v0.1 (2011-6-14 基本功能版 spring3+struts2+hibernate3+jpa)
1. WebOS界面
2. js、css文件动态加载
3. 表单客户端验证
4. 视图工具条、分页、简单搜索、本地排序与远程排序、导出为Excel
5. highchart图表整合
6. 视图表单CRUD操作基本封装
7. 组织架构管理：单位、部门、岗位
8. 用户管理
9. 角色权限管理：资源配置、权限分配
10. 个性化配置：自定义主题(使用jquery-ui的CSS框架体构的主题)、自定义字体大小(可在12px、14px、16px、18px、20px间切换)
11. 自定义桌面：可自定义桌面的快捷方式，如增加外部链接