# xuanyuan
- 一个javase程序 
 
## 快速开始
- 克隆本项目
- 开始开发，程序入口:cn.enilu.xuanyuan.application.Main

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
