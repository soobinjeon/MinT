# Software and System Development Kit

## Introduction
LapThings프로젝트의 핵심 요소 중 하나이다. IoT 환경에서 운영되는 센서모듈 또는 노드의 설계 및 제작을 쉽게 할 수 있는 프레임워크 개발을 목적으로 하는 프로젝트. BeagleBone Black을 기반으로 한다.

## Build Server
* Server IP : 210.115.47.194
* Port : 8022
* ID : root
* PW : snslab

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
	> git clone http://요비ID@sn.kangwon.ac.kr/LabThings/SnSDK

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
## 1. Install the SnSDK to Beagle Bone Black
### [SnSDK Installation Guide for Beagle Bone Black](http://sn.kangwon.ac.kr/LabThings/SnSDK/post/11)
## 2. Development Information
### Link for [Framework Developer](http://sn.kangwon.ac.kr/LabThings/SnSDK/post/8)
### Link for [Driver Developer](http://sn.kangwon.ac.kr/LabThings/SnSDK/post/9)
### Link for [Application Developer](http://sn.kangwon.ac.kr/LabThings/SnSDK/post/10)
## 3. Project
### 3.1 Driver Project
1. HT01SV Sensor Driver Example Project -> [SnSDK_Example_driver-HT01SV](http://sn.kangwon.ac.kr/LabThings/SnSDK_Example_Driver-HT01SV)

### 3.2 Application Project
1. Application Example Project -> [SnSDK_Exam_Application](http://marsberry@sn.kangwon.ac.kr/LabThings/SnSDK_Exam_Application)

## Latest Edit
> 02/26/2015