# Middleware for Cooperative Interactions of Things

## Introduction
다양한 시스템 환경에서 사물들이 서로 상호작용 할 수 있는 통합 미들웨어 플랫폼.
본 프레임워크는 다양한 종류의 센서 및 네트워크 디바이스를 적용할 수 있는 개발 환경을 제공 한다. 
개발된 드라이버와 Application API를 통해 쉽게 IoT 어플리케이션을 제작 할 수 있다. 
다양한 플랫폼에 적용 가능하기 때문에 IoT 환경에 적합한 디바이스 모듈 제작이 가능하다.

* 개발 가능 플랫폼
	1. Beagle Bone Black
	2. Raspberry PI(I, II)
	3. Android Platform
	4. Linux Based Platform

## Build Server
* Server IP : -
* Port : -
* ID : -
* PW : -

## Development Environment
* IDE
	1. Netbeans
	2. VIM
* Build OS
	1. Beagle Bone Black Debian
* Build Language
	1. JAVA
	2. C with JNI

## Git을 이용하여 Framework 저장소 가져오기
* Linux
	> git clone http://요비ID@sn.kangwon.ac.kr/LabThings/MinT

* Window
	> git을 설치한 후 원하는 폴더에 마우스 오른쪽 버튼을 눌러 Clone

## netbeans에서 프로젝트 가져오기
> 1. File -> Open Project
> 2. git으로 내려받은 프로젝트 위치를 찾는다.
> 3. Open Project 클릭

## 프로젝트 폴더 설명
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

# Getting Start
## 1. Install the MinT to Beagle Bone Black
### [MinT Installation Guide for Beagle Bone Black](http://sn.kangwon.ac.kr/LabThings/MinT/post/11)
## 2. Development Information
### Link for [Framework Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/8)
### Link for [Driver Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/9)
### Link for [Application Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/10)
## 3. Project
### 3.1 Driver Project
1. HT01SV Sensor Driver Example Project -> [MinT_Example_driver-HT01SV](http://sn.kangwon.ac.kr/LabThings/MinT_Example_Driver-HT01SV)

### 3.2 Application Project
1. Application Example Project -> [MinT_Exam_Application](http://marsberry@sn.kangwon.ac.kr/LabThings/MinT_Exam_Application)

## 4 Latest Update - MinT version 2.3

### 4.1 주요 업데이트 내용

* Network Layer
	* 네트워크 레이어 생성
	* Network Protocol -> Matcher/Serialization -> Transport -> System handle
	* 각 스레드 풀에 따라 동작됨
* Thread Pool
	* All fixed Thread size
	* System Thread Pool (Threads : 10)
		* 시스템 관리 스레드 풀
	* Receive Thread Pool (Threads: 50, Queue: 250000, Task class: RecvMSG.java)
		* receive 관리 풀
		* network listener -> |queuing| -> Matcher -> Transporter -> System Handler
	* Send Thread Pool (Thread: 1, Queue: 250000, Task class: SendMSG.java)
		* send 관리 풀
		* 1개의 스레드밖에 동작이 안되고 있음
			* UDP Sender가 포트 1개당 1개밖에 동작 안됨(여러개가 되지만 속도는 같음)
		* System Handler -> |queuing| -> Transporter -> Serialization -> network sender

* Packet Diagram
	* 기존 String Array 에서 byte Array로 변경 완료
	```java
	 * Packet Protocol for MinT
	 * MinT Protocol
	 * {DIR|INS|ID}{source}{final destination}{msg data}
	 * |-header---||----------route----------||--data--|
	 *            || address(ip:port, ble)   ||        | should make maximum size
	 *  - MESSAGE HEADER (total 5byte)
	 *     - DIR : REQUEST(0), RESPONSE(1) (1 bit)
	 *     - INS : GET(0), SET(1), POST(2), DELETE(3), DISCOVERY(4) (3 bit)
	 *     - ID  : (4byte)
	```
* Performance Evaluation
	* 성능 측정을 위한 클래스 구현
	* Benchmark package

### 4.2 버전 정보 및 소스

- [MinT v2.3](http://sn.kangwon.ac.kr/LabThings/MinT/code/refs%252Ftags%252Fv2.3)

## Latest Edit
> 09/17/2015
> 08/24/2016