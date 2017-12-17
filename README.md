# Hisoka
Java web应用开发的基础服务框架.

## 作者
* Hinsteny [Home](https://github.com/Hinsteny)

### 项目介绍
项目本身采用Maven管理, jdk1.8, 三方项目以jar包的方式引入此服务框架.

### 包含技术
*  Spring, Springmvc, dubbo, mybatis, postgres. 


### 配置使用
* git clone git@github.com:Hinsteny/Hisoka.git
* 编译安装到本地或者自己可以访问到的maven仓库 ('mvn clean compile install -Dskiptest=true')
* 在所要引入的工程项目里引入即可


...

### 日志系统
*  采用以slf4j为接口, 实现用log4j2, 请在使用时需要在引入的项目资源根目录添加 log4j2.xml 配置文件
* 