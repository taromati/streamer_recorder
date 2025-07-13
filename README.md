# Streamer Recorder 사용법

Streamer-Recorder를 이용하여 인터넷 방송을 지속적으로 녹화할 수 있습니다.

## **사전 설치**

1. Python 3.10 이상 설치 [Download Python](https://www.python.org/downloads/)
2. Java 21 설치 [Download Java](https://aws.amazon.com/corretto/)
3. Streamlink 설치 [Download Streamlink](https://streamlink.github.io/install.html)


## **빌드 방법**

1. 리눅스/맥 : 터미널에서 `gradlew clean assemble bootJar` 실행
2. 윈도우    : 터미널에서 `gradlew.bat clean assemble bootJar` 실행
    

## **실행 방법**

1. application.yml.example → application.yml 로 변경 및 내용 수정
   ```yml
   discord: # 디스코드 봇 설정
      token:
      server-name:
      channel-name:
   logging: # 로깅 설정
      level:
         me.taromati.streamerrecorder: INFO # 상세로그를 보고싶을 때 DEBUG로 변경
         org.springframework: WARN
   record:
      file-dir: /PATH/TO/YOUR/RECORD/DIR #저장될 폴더 위치
      soop:
         username: YOUR_USERNAME #아프리카 아이디
         password: YOUR_PASSWORD #아프리카 비밀번호
    ```
2. jar 파일과 application.yml 파일을 같은 폴더에 위치
   - jar 파일은 build/libs/ 폴더에 위치
3. 터미널에서 `java --enable-preview -jar streamer-recorder-1.0.0.jar` 실행


## **사용 방법**

1. [http://localhost:6060](http://localhost:6060) 혹은 [http://127.0.0.1:6060](http://127.0.0.1:6060) 접속
2. 녹화할 스트리머 정보 입력 후 저장
   ```
   플랫폼 종류: CHZZK, AFREECA, TWITCASTING, TWITCH
   user_id: 각 플랫폼별 스트리머 고유값
   user_name: 녹화 파일에 넣을 스트리머명
   ```
