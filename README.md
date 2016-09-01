# [BC 平台](https://github.com/bcsoft/bc-framework)
基于 SSH 框架的多窗口 Single Page Application。
![](docs/index.png)

## 检出并编译源代码的步骤
1. 检出并安装[第三方依赖包](https://github.com/bcsoft/bc-3rd-party-jar)到本地 maven 仓库
```
$ git clone https://github.com/bcsoft/bc-3rd-party-jar.git
$ cd bc-3rd-party-jar
$ ./3rd-party-jar_install2local.sh
```

2. 检出 BC 平台源码并编译安装
```
$ git clone https://github.com/bcsoft/bc-framework.git
$ cd bc-framework
$ git submodule init
$ git submodule update
$ mvn clean install -Dmaven.test.skip=true
```

## Issue Tracking
通过[本项目的 GitHub Issues](https://github.com/bcsoft/bc-framework/issues) 页面提交 Issue。

## License
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 版本升级说明
如果版本号要升级，需修改如下几处地方：
- pom.xml 中属性 version 和 framework.version 的值
- bc-framework-bom/pom.xml 中属性 version 和 framework.version 的值
