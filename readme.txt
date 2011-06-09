bc平台基础架构

浏览地址： https://github.com/rongjihuang/bc-framework
源码检出： git@github.com:rongjihuang/bc-framework.git

一) 平台编译发布需要的工具
ant1.8+、maven3+、java1.5+

二) 生成数据库的构建脚本：
    >ant build
    运行后会在build目录下生成如下名称的sql文件
    1) db.[dbtype].drop.sql 删除数据库表的脚本
    2) db.[dbtype].create.sql 数据库的建表脚本
    3) db.[dbtype].data.sql 数据库的初始化数据
    * [dbtype]可能的值为mysql、oracle、mssql，目前仅mysql可用。

二) 编译发布步骤(mysql)：
    1) 用mysql命令行创建名为bcdemo的数据库，分配用户bcdemo，密码也设为bcdemo
	   mysql命令可参考 http://rongjih.blog.163.com/blog/static/3357446120114623715117/
    2) 用上面生成的sql脚本初始化数据库
       >mysql -ubcdemo -pbcdemo bcdemo < db.mysql.drop.sql
       >mysql -ubcdemo -pbcdemo bcdemo < db.mysql.create.sql
       >mysql -ubcdemo -pbcdemo bcdemo < db.mysql.data.sql
    3) 执行>mvn clean install -Dmaven.test.skip=true