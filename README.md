# Middleware for Cooperative Interactions of Things (MinT)

## Introduction
We propose an IoT middleware platform to support the cooperative interaction of things called MINT (Middleware for Cooperative INteraction of Things). The characteristics of cooperative interaction can be used to effectively configure the IoT working environment. These are the goals of MINT:
- To support various sensors and network devices platforms
- Enable connections between heterogeneous networks   
- Provide for fast connections and effective interaction between objects
- Enable easy installation and provide a simple development platform
- Support Efficiency performance management for limited objects 

![IoT Context diagram on MinT](http://i.imgur.com/KBCGKjk.png "Yobi")

The figure shows the context diagram of IoT working environment as implemented on the MinT platform. The function of the main platform is to manage and operate the devices connected in the IoT. The sensors and network devices are co-worked for collecting information from the surroundings and then delivering the information to other objects or environments. 

The MinT provides the SAL (Sensor Abstraction Layer) for various sensors. The capabilities of the SAL aid in effectively connecting sensors and network devices with each other, and managing them as a system. The MinT supports both Linux and Windows platforms. In addition, the MinT provides interfaces like GPIO, ADC, SPI, and UART to connect various types of hardware including the Beagle Bone Series, Raspberry Pi Series, and the Edison with multiple types of sensors. These interfaces aid in rapid and convenient development of IoT networks. 

The objects in MinT can interact with the other objects with which they are connected. The IoT environment supports various communication protocols such as Bluetooth, BLE, ZigBee, and Wi-Fi. When a united network environment is installed, connectivity can be established between the objects via any of the protocols based on the quality of the physical network connection and flexible usage of system resources. The MinT platform supports functionality related to the unified network environment needed to connect objects via various network protocols. 
Basically, the MinT platform uses CoAP because this protocol is well suited for IoT environments employing limited resources. 

The MinT provides powerful development tools for service developers and clients. By using these tools, users can develop new service applications, those that are standalone in nature and those that cooperate with other services to implement a new service. Given the fact that the MinT platform is easy to learn and has a user-friendly interface, besides expert developers, even general users can rapidly take high-level business ideas from concept to implementation in an IoT environment. The reduced development time has a favorable effect on the ability of organizations to implement IoT systems with the latest sensor devices and service applications.

## Technical Notes 
* [System Architecture](http://sn.kangwon.ac.kr/LabThings/MinT/post/22)
* [MinT Performance Evaluation](http://sn.kangwon.ac.kr/LabThings/MinT/post/23)
* [Flexible performance improvement](http://sn.kangwon.ac.kr/LabThings/MinT/post/24)
* [Integration of Heterogeneous network](http://sn.kangwon.ac.kr/LabThings/MinT/post/25)

## Development Environment
* Available on
	1. Beagle Bone Black
	2. Raspberry PI(I, II, III)
	3. Intel Edison
	3. Android Platform
	4. Linux Based Platform
* Build IDE
	1. Netbeans
* Build OS
	1. Windows
	2. Linux based
	3. Android
* Build Language
	1. JAVA
	2. C

## Get Clone with Git
* Yobi
	> git clone http://sn.kangwon.ac.kr/LabThings/MinT
* Github
	> git clone https://github.com/soobinjeon/MinT.git

## Open Project with netbeans
> 1. File -> Open Project
> 2. Find clone folder for git clone
> 3. Click Open Project

## Project Directory
* ***conf***
	- Configuration for using MinT on platforms (Beagle Bone Black, Raspberry Pi, and so on)
* ***doc***
	- Project Documents
* ***driverTemplete***
	- Templete for driver developer
* ***MinT***
	- MinT source
* ***usr***
	- Library for MinT

# Getting Start
## 1. Install the MinT to Platform
### [MinT Installation Guide for Beagle Bone Black](http://sn.kangwon.ac.kr/LabThings/MinT/post/11)
### [MinT Installation Guide for Raspberry Pi 3 - not ready but similar with beagle bone black]
### [MinT Installation Guide for Intel Edison - not ready but similar with beagle bone black]
## 2. Development Information
### Link for [Framework Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/8)
### Link for [Driver Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/9)
### Link for [Application Developer](http://sn.kangwon.ac.kr/LabThings/MinT/post/10)
## 3. Tutorial Projects
### 3.1 Driver Project
1. DHT11 Sensor Driver Example Project -> [MinT_Example_driver-DHT11](http://sn.kangwon.ac.kr/LabThings/MinT_DHT11_Control)
2. HT01SV Sensor Driver Example Project -> [MinT_Example_driver-HT01SV](http://sn.kangwon.ac.kr/LabThings/MinT_Example_Driver-HT01SV)
3. SRF04 Sensor Driver Example Project -> [MinT_Example_driver-SRF04](http://sn.kangwon.ac.kr/LabThings/MinT_SRF04_Demo/settingform)
4. HM10-BLE Sensor Driver Example Project -> [MinT_Example_driver-HM10-BLE](http://sn.kangwon.ac.kr/LabThings/HM10-BLE_MinT_DeviceDriver)
5. You can see more example in -> [LabThings Projects](http://sn.kangwon.ac.kr/organizations/LabThings)

### 3.2 Application Project
1. Application Example Project -> [MinT_Exam_Application](http://sn.kangwon.ac.kr/LabThings/MinT_Sharing)

## 4 Latest Update - MinT version 2.8

### 4.1 주요 업데이트 내용
* Re-define CoAP details #35, #38, #39, #40 
	* Added CON, NON message token for CoAP packets @aa14ac0
	* re-transmission for CON message in CoAP @a1e40c8
		* update re-transmission construction @1432006
	* Updated multicast method for above updating @4d8bb33, @15cb5c1
	* update packet algorithm to distinguish the multicast or unicast during receiving the packet. @804d533
	* Added CoAP leisure for multicast @7a4447f

* CoAP Protocol
	* Some Support CoAP Protocol
	```java
	 * Packet Protocol for MinT
	 * MinT Protocol
	 * {DIR|INS|ID}{source}{final destination}{msg data}
	 * |-header---||----------route----------||--data--|
	 *            || address(ip:port, ble)   ||        | should make maximum size
	 * 
	 * CoAP Header (4 bytes)
	 * 0                   1                   2                   3
	    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |Ver| T |  TKL  |      Code     |          Message ID           |
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |   Token (if any, TKL bytes) ..,
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |   Options (if any) ...,
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	   |1 1 1 1 1 1 1 1|    Payload (if any)...
	   +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 
	 * 
	```

* v2.8 Link - [Click Here](http://sn.kangwon.ac.kr/LabThings/MinT/code/refs%252Ftags%252Fv2.8)

### 4.2 버전 정보 및 소스
- [MinT v2.8-update](http://sn.kangwon.ac.kr/LabThings/MinT/post/21), [MinT v2.8-releases](http://sn.kangwon.ac.kr/LabThings/MinT/code/refs%252Ftags%252Fv2.8)
- [MinT v2.7-update](http://sn.kangwon.ac.kr/LabThings/MinT/post/19), [MinT v2.7](http://sn.kangwon.ac.kr/LabThings/MinT/code/refs%252Ftags%252Fv2.7)
- [MinT v2.4](http://sn.kangwon.ac.kr/LabThings/MinT/post/18)
- [MinT v2.3](http://sn.kangwon.ac.kr/LabThings/MinT/post/17)

## Publications

* International Journals
	- Soobin Jeon, Inbum Jung, "Middleware_for_Cooperative_Interaction_of_things," proceeding in journals (SCI(E)).
	- Soobin Jeon, Inbum Jung, "Experimental Evaluation and flexible performance improvement for CoAP-Enabled IoT Platform," Proceeding in journals (SCI(E)).

* Domestic Journals
	- 전수빈, 한영탁, 이충산, 정인범, "이기종 시물간의 상호작용을 위한 통합 미들웨어 플렛폼," acceptance in 정보과학회논문지
	- 전수빈, 한영탁, 이충산, 정인범, "CoAP 기반의 사물인터넷 플랫폼 실험 평가 및 효율적 성능 향상 방법," proceeding in journal
	- 이충산, 한영탁, 전수빈, 서동만, 정인범, "사물인터넷에서 초음파 센서와 블로투스 통신을 이용한 스마트 주차 시스템," 정보과학회 컴퓨팅의 실제, 22권, 6호, pp. 268-277, 2016년 06월

* International Conference
	- Soobin Jeon, Chungsan Lee, Youngtak Han, Dongmahn Seo and Inbum Jung, "The Smart Shoes Providing the Gait Information on IoT," 2017 IEEE International Conference on Consumer Electronics (ICCE), pp. 96-97, Jan, 2017
	- Youngtak Han, Chungsan Lee, Youjin Kim, Soobin Jeon, Dongmahn Seo, Inbum Jung, "Smart Umbrella for Safety Directions on Internet of Things," 2017 IEEE International Conference on Consumer Electronics (ICCE), pp. 84-85, Jan, 2017
	- Soobin Jeon, Chungsan Lee, Youngtak Han, Dongmahn Seo and Inbum Jung, "MinT: Middleware for Cooperative Interactions of Things," 2016 IEEE International Conference on Consumer Electronics (ICCE), pp. 139-140, Jan, 2016
	- Chungsan Lee, Youngtak Han, Soobin Jeon, Dongmahn Seo, Inbum Jung, "Smart Parking System for Internet of Things," 2016 IEEE International Conference on Consumer Electronics (ICCE), pp. 289-290, Jan, 2016

* Domestic Conference
	- 김유진, 이충산, 전수빈, 정인범, "실내 사물인터넷 환경을 위한 객체 크기 기반의 사람 검출 방법," 한국 정보과학회 2016년 동계학술발표회, pp. 1507-1509, 2016년 12월
	- 신세정, 이충산, 한영탁, 전수빈, 정인범, "실시간 현황갱신 및 사물 간 상호작용을 위한 인아웃보드 시스템," 한국 정보과학회 2016년 동계학술발표회, pp. 1570-1572, 2016년 12월
	- 한영탁, 이충산, 전수빈, 정인범, "IoT 개발 환경을 위한 통합 디바이스 제어 도구," 한국정보과학회 2015 동계학술발표회, pp. 348-350, 2015년 12월
	- 이기웅, 전수빈, 이충산, 한영탁, 심순용, 이은수, 정인범, "사물인터넷을 이용한 스마트 우산," 한국정보과학회 2015 동계학술발표회, pp. 428-430, 2015년 12월
	- 이은수, 전수빈, 이충산, 한영탁, 심순용, 이기웅, 신세정, 정인범, "사물인터넷 기반의 스마트 조명시스템," 한국정보과학회 2015 동계학술발표회, pp. 446-448, 2015년 12월
	- 이충산, 한영탁, 전수빈, 정인범, "사물 인터넷을 위한 이기종 네트워크 통합 프레임워크," 한국정보과학회 2015 동계학술발표회, pp. 443-445, 2015년 12월
	- 심순용, 전수빈, 이충산, 한영탁, 이기웅, 이은수, 정인범, "사물인터넷 기반 스마트 신발," 한국정보과학회 2015 동계학술발표회, pp. 449-451, 2015년 12월
	- 한영탁, 전수빈, 이충산, 정인범, "IoT 환경에서 화분을 관리하기 위한 인터넷 가든 시스템," 2015년 한국컴퓨터종합학술대회, pp. 463-465, 2015년 06월
	- 이충산, 전수빈, 한영탁, 정인범, "사물 인터넷에서의 디바이스 개발을 위한 통합 플랫폼," 2015년 한국컴퓨터종합학술대회, pp. 471-473, 2015년 06월
	- 이충산, 한영탁, 조영태, 전수빈, 최진섭, 정인범, "사물 인터넷을 위한 지능형 주차 시스템," 2014년 한국컴퓨터종합학술대회, pp. 1842-1844, 2014년 06월

* patent
	- 스마트폰과 블루투스를 이용한 주차 관리 및 안내 방법, 출원일 2013. 12. 31

## Latest Edit
> 09/17/2015
> 08/24/2016
> 10/10/2016
> 11/16/2016
> 12/12/2016
> 02/20/2017