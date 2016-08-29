# 文档转换服务端
用于将 MS Office 文档转换为 pdf 文档。

## 打包部署
1. 打包 `mvn clean package -Pproduct`
2. 启动 `java -classpath bc-rmi-1.0.jar cn.bc.rmi.server.ConsoleMain`

## 开发调试
1. 下载 [jacob 1.16.1](http://nchc.dl.sourceforge.net/project/jacob-project/jacob-project/1.16.1/jacob-1.16.1.zip)
2. 将 jacob-1.16.1.zip 包内的文件 jacob-1.16.1-x64.dll (64位win) 或 jacob-1.16.1-x86.dll (32位win) 复制到 JAVA_HOME/bin 目录下
3. 安装 Microsoft Office 2010 | 2013
4. 启动转换服务 `mvn jetty:run`
5. ok

## 参考资料
- [利用 Jacob 调用 MS Office 转换文档为 PDF](http://www.cnblogs.com/luckyxiaoxuan/archive/2012/06/13/2548355.html)
- [通过 JACOB 实现 Java 与 COM 组件的互操作](http://www.ibm.com/developerworks/cn/java/j-lo-jacob/index.html)

## 其他

### Ubuntu
1. 将宿主机的文件夹"/bcfile/bcdata"只读共享给客户机，并指定共享名称为“FromRootDir”：
```
$ VBoxManage sharedfolder add win2003sp2 --name FromRootDir --hostpath /bcfile/bcdata --readonly
($ VBoxManage sharedfolder remove win2003sp2 --name FromRootDir)
```
2. 将宿主机的文件夹"/bcfile/bcdata/convert"共享给客户机，并指定共享名称为“ToRootDir”：
```
$ VBoxManage sharedfolder add win2003sp2 --name ToRootDir --hostpath /bcfile/bcdata/convert
($ VBoxManage sharedfolder remove win2003sp2 --name ToRootDir)
(查看已经共享的文件：$ VBoxManage showvminfo win2003sp2)
```

### Window
在 Windows  2003 系统中可以通过命令行将网络共享的文件夹映射为网络驱动器：(注意路径区分大小写)
```
> net use E: \\VBOXSVR\FromRootDir
> net use F: \\VBOXSVR\ToRootDir
```

以命令行的方法删除网络驱动器：(“以管理员身份运行”来打开命令提示符)
```
> net use x:/delete (其中 x: 是网络驱动器的盘符)
```