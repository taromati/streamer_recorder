# Streamer Recorder 사용법

Streamer-Recorder를 이용하여 생방송 다운받는 방법 안내

## **📘사전 설치**

1. python 3.12 이상 설치
2. JAVA 21 설치 [Download the Latest Java LTS Free](https://www.oracle.com/kr/java/technologies/downloads/)
3. 스트림링크 설치 `pip install --user -U streamlink`

## **📘설치**

생방송 다운로드를 위한 설치 방법 안내

1. 압축 받은 폴더 압축 해제
2. application.yml.example → application.yml 로 변경
3. application.yml 내용 수정 
    
    ```java
    record: 
    	file-dir: C:\Users\user\Desktop //저장될 폴더 위치 
    	afreeca: 
    		username: //아프리카 아이디 
    		password: //아프리카 비밀번호
    ```
    

## **📘사용 방법**

1. 터미널에서 경로 이동
2.  `java --enable-preview -jar streamer-recorder-1.0.0.jar`
3. [localhost:6060](http://localhost:6060) 혹은 127.0.0.1:6060 접속
4. 아래의 내용 선택 후 저장 

```java
플랫폼 종류
  - TWITCH
  - CHZZK
  - AFREECA
  - TWITCASTING

user_id: 각 플랫폼별 스트리머 고유값
user_name: 녹화 파일에 넣을 스트리머명
```
