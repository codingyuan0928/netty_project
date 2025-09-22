# 1️⃣ 使用 OpenJDK 17 作為基底
FROM openjdk:17-jdk

# 2️⃣ 設定工作目錄
WORKDIR /app

# 3️⃣ 複製 JAR 檔案
COPY target/netty_practice-0.0.1-SNAPSHOT.jar app.jar

# 4️⃣ 直接使用環境變數，避免 `application.properties` 覆蓋問題
ENV SPRING_DATASOURCE_URL=jdbc:mysql://some-mysql:3306/netty_practice
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=123456
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV NETTY_SERVER_HOST=netty-server-container
ENV NETTY_SERVER_PORT=9090

# 5️⃣ 設定容器啟動指令
CMD ["java", "-jar", "app.jar"]

# 6️⃣ 暴露 8083 端口
EXPOSE 8083
