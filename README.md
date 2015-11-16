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

## Latest Edit
> 09/17/2015