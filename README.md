# xuanyuan
- 日常工具库

## 模块
- application:程序入口，业务代码在该模块编写
- lib：一个空模块，包装其他所有基础模块，方便外部程序试用，外部程序使用的话，可以只引入这一个模块
- core：基础模块，常见工具包
- net：网络相关操作，httpclient，ftp
- pdf：pdf操作，转换word，html，txt等
- solr： 操作solr数据库
 
## 快速开始
- 克隆本项目
- 开始开发，程序入口:cn.enilu.xuanyuan.application.XuanyuanApplication

## 快速部署
- 打包 mvn package -DskipTests
- 在部署到的服务器上创建程序目录:xuanyuan
- 将xuanyuan-application/lib目录拷贝至服务器上xuanyuan/lib
- 将xuanyuan-application/target/xuanyuan-application-1.0.jar 拷贝至服务器上xuanyuan/lib
- 将bin/start.sh 和stop.sh 拷贝至服务器上xuanyuan/目录
- 在服务器上创建配置文件目录xuanyuan/conf
- 将xuanyuan-core/src/main/resources/目录下配置文件上传到上一步的conf目录中
- 在服务器上创建日志目录xuanyuan/log
- 启动项目：sh start.sh
- 停止项目：sh stop.sh
