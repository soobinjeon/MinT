# Software and System Development Kit

## 개요
LapThings프로젝트의 핵심 요소 중 하나이다. IoT 환경에서 운영되는 센서모듈 또는 노드의 설계 및 제작을 쉽게 할 수 있는 프레임워크 개발을 목적으로 하는 프로젝트. BeagleBone Black을 기반으로 한다.

## Development Information

### Build Server
> Server IP : 210.115.47.194
> Port : 8022
> ID : root
> PW : snslab

### 개발 환경
* IDE
	1. Netbeans
	2. VIM
* Build OS
	1. Beagle Bone Black Debian
* Build Language
	1. JAVA
	2. C with JNI

### Git을 이용하여 Framework 저장소 가져오기
* Linux
	> git clone http://요비ID@sn.kangwon.ac.kr/LabThings/SnSDK

* Window
	> git을 설치한 후 원하는 폴더에 마우스 오른쪽 버튼을 눌러 Clone

### netbeans에서 프로젝트 가져오기
> 1. File -> Open Project
> 2. git으로 내려받은 프로젝트 위치를 찾는다.
> 3. Open Project 클릭

### 프로젝트 폴더 설명
* ***conf***
	- Beagle Bone Black Debian 환경에서 Framework를 사용하기 위한 환경변수 설정
* ***doc***
	- 프로젝트 문서
* ***driverTemplete***
	- 드라이버 개발자를 위한 Templete 파일
* ***framework***
	- 프레임워크 소스
* ***usr***
	- 프레임워크 개발 및 사용에 필요한 라이브러리

## Framework 개발자
### 개발
기본적으로 Netbeans 또는 VIM을 이용하여 개발한다.
### 빌드방법
빌드를 위해서는 아래의 규칙을 지켜 수행한다.
* **빌드 및 런타임 위치는 SnSDK 프로젝트 폴더 안에 넣지 않는다.**
* 빌드 및 런타임 위치 수정
	- JAVA는 플랫폼의 영향을 받지 않기 때문에 BBB에서 빌드할 필요가 없다 그러므로 Netbeans를 이용하여 윈도우로 빌드한다.
	1. Netbeans
		- Files 탭 클릭
		- 프로젝트 폴더 -> nbproject -> project.properties 클릭
		![Capture1.JPG](/files/225)
		- 아래 내용을 수정하여 빌드 위치 수정
			> //빌드 위치
			> build.dir=build
			> //아래와 같이 수정 할 수 있다.
			> build.dir=../javatest/build //상위 폴더에 javatest폴더를 생성하여 build폴더에 빌드
			>
			> //라이브러리 또는 런타임 파일 생성 위치
			> dist.dir=dist
			> //이것도 다른 폴더로 수정
			> dist.dir=../javatest/dist
	2. Linux
		- 알아서 찾아서 하삼
			> //컴파일 예제
			> javac -d directory_location java.c

### Framework 라이브러리 파일(*.jar) 생성
* 기본적으로 프로젝트 빌드 시 dist 폴더에 jar 파일 생성

### Framework 프로젝트 테스트를 위한 실행 방법
* Framework는 라이브러리로 개발되기 때문에 따로 실행 할 일은 없다.
* 개발된 Framework의 동작을 확인하기 위해서는 따로 main 프로젝트를 생성하여 Framework 라이브러리를 추가한 후 테스트 한다.
* ***Framework 프로젝트 테스트 방법***
	1. Java Application 프로젝트 생성
		- 주의 : SnSDK 폴더안에 생성하지 않는다. TEST용으로 따로 폴더를 만들어 사용
	2. Framework 프로젝트에서 생성된 library(*.jar)를 참조한다.
	3. 테스트할 라이브러리 메소드 소스 코딩
	4. Netbeans에서 실행 방법
		- 로컬에 실행
			- 그냥 실행 하면 됨
		- Build Server에서 실행
			1. Remote Library 생성
				- remote library를 생성하면 BBB에 원격으로 실행이 가능함
				- 설정 방법 : https://netbeans.org/kb/docs/java/javase-embedded.html
				- 설정은 아래와 같이 하면 됨
				![Capture2.JPG](/files/226)
			2. 생성된 Remote 환경에서 Run 하기
				- 프로젝트 설정 -> Run 클릭
				- RunTime Platform을 생성한 Remote 플랫폼으로 변경하면 됨
				![Capture3.JPG](/files/227)
				- 변경후 Run 하면 원격 플랫폼에서 실행됨

## 드라이버 개발자
드라이버 개발자는 2가지의 개발 환경에서 개발을 진행한다.
첫번째, 센서의 컨트롤을 위한 C 개발 환경
두번째, Framework와의 연결을 위한 JAVA 개발 환경
마지막으로 이 둘을 연결하기 위한 JNI

### 드라이버 개발 단계
1. Framework 빌드 및 Library 파일 생성
2. 센서 드라이버 JAVA 파일 생성
2. Framework 라이브러리 연결
5. 센서 컨트롤을 위한 Device 클래스 상속 및 Method 생성
6. 센서 컨트롤을 위한 JNI 파일(*.h) 생성
7. 생성된 헤더파일을 이용하여 센서 컨트롤 소스 코딩
8. 컴파일 및 라이브러리 생성
9. 테스트

## 센서노드 개발자
센서노드 개발자

## Latest Edit
> 02/22/2015