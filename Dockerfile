FROM debian:bookworm-slim
RUN apt update && apt install -y dpkg wget dos2unix && apt autoremove -y
RUN wget https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.deb
RUN dpkg -i ./jdk-24_linux-x64_bin.deb
RUN apt install ./jdk-24_linux-x64_bin.deb
WORKDIR /salon
COPY . .
RUN dos2unix gradlew
RUN chmod +x gradlew
RUN bash ./gradlew build
RUN bash ./gradlew shadowJar
CMD ["sh", "-c", "java -jar $(ls ./build/libs/Chesnay-bot-*-all.jar | tail -n 1)"]
