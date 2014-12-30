一）打包部署：
1. 打包命令：
>mvn clean assembly:assembly

2. 启动服务：
>java -classpath bc-rmi-1.0.jar cn.bc.rmi.server.ConsoleMain

二）开发调试：
1. 下载 jacob 1.16.1
http://nchc.dl.sourceforge.net/project/jacob-project/jacob-project/1.16.1/jacob-1.16.1.zip
2. 将 jacob-1.16.1.zip 包内的文件 jacob-1.16.1-x64.dll 复制到 JAVA_HOME/bin 目录下
如果是32位的Windows系统，则为文件 jacob-1.16.1-x86.dll
3. 安装 Microsoft Office 2010
4. 启动转换服务
> mvn jetty:run
5. ok

三）参考资料
利用Jacob调用MS Office转换文档为PDF: http://www.cnblogs.com/luckyxiaoxuan/archive/2012/06/13/2548355.html
通过 JACOB 实现 Java 与 COM 组件的互操作: http://www.ibm.com/developerworks/cn/java/j-lo-jacob/index.html