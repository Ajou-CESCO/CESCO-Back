# 기본 이미지를 Zulu JDK 17로 설정
FROM azul/zulu-openjdk:17

# 작업 디렉토리 설정
WORKDIR /app

# 호스트 시스템의 ./app 디렉토리를 컨테이너의 /app 디렉토리로 복사
COPY ./PillinTime /app

# Gradle Wrapper 파일 복사
COPY ./PillinTime/gradlew /app/
COPY ./PillinTime/gradle /app/gradle

# gradlew 실행권한 부여
RUN chmod +x /app/gradlew

# Gradle 종속성을 미리 다운로드
RUN /app/gradlew --version

# DOS 줄바꿈 문자를 Unix 형식으로 변환
RUN apt-get update && apt-get install -y dos2unix && \
    find . -type f -exec dos2unix {} \;

# Gradle 빌드 실행
RUN ./gradlew clean build

# 애플리케이션을 빌드하고 실행하는 명령어 설정
CMD ["./gradlew", "bootRun"]
