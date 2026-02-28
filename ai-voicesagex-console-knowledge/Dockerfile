FROM 127.0.0.1:8999/middleware/openjdk21:latest
ARG JAR_NAME
ADD target/${JAR_NAME}.jar /app.jar
# 设置编码、时区
ENV LANG C.UTF-8
ENV LC_ALL C.UTF-8
ENV LANGUAGE C.UTF-8
ENV TIME_ZONE Asia/shanghai
RUN  ln -snf /usr/share/zoneinfo/$TIME_ZONE /etc/localtime
ENTRYPOINT ["nohup","java","-Dfile.encoding=UTF-8","-Duser.timezone=Asia/Shanghai","-Djava.awt.headless=true","-Djava.net.preferIPv4Stack=true","-server","-Xmx2048m","-Xms2048m","-Xmn1024m","-Xss2m","-XX:HeapDumpPath=./data1/logs/ai-voicesagex-console/heapdump/knowledge/","-Dproject.name=ai-voicesagex-console-knowledge","-jar","app.jar",">/dev/null 2>&1 &"]
