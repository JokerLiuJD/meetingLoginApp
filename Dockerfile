# 使用 AdoptOpenJDK 的 JRE 镜像作为基础镜像
FROM ubuntu:latest

# 创建目录
RUN mkdir -p /dashboard
# 拷贝文件
COPY ./target/my-demo-app.jar /dashboard/my-demo-app.jar

# 拷贝静态文件夹
COPY static /static

# 暴露应用端口
EXPOSE 8080


# 更新包列表并安装所需软件包和Noto Sans JP字体,OpenJDK 17
RUN apt-get update && \
    apt-get install -y apt-utils && \
    apt-get install -y fonts-noto-cjk openjdk-17-jre && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*




# 启动应用
CMD ["java", "-jar", "/dashboard/my-demo-app.jar"]