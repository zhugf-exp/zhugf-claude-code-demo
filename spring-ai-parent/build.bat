@echo off
echo ===========================================
echo Spring AI 项目构建脚本
echo ===========================================

echo 使用独立Maven配置，绕过内部仓库...

REM 设置Java版本
set JAVA_HOME=C:\Program Files\Java\jdk-25.0.4.1

REM 清理并编译项目
mvn clean compile -s settings-final.xml

echo.
echo 如果编译成功，可以运行：
echo mvn spring-boot:run -pl quick-start -s settings-final.xml
echo ===========================================