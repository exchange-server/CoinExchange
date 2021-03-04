Open source digital currency trading system

This source code is limited to exchange and study, where it involves legal issues and I have nothing to do with it
=====================================
- ## [简体中文](README.md)
---

## Questions and suggestions
- Using Issuse, we will follow up and answer in time.
- Join the exchange group: Blockchain exchange technology knowledge exchange group [QQ group: 530862680][QQ:247818019]
- E-mail: 247818019@qq.com

# Donate:
#### Your donation is our biggest motivation for open source
- ETH/USDT：0xE4Be628D596995f2eB91E78392aD4976ec6BEaa1

#### ZTuo is a free and open source digital currency trading system, which is developed using the most popular Java framework and related technologies.

# Vision
     Our mission is to develop the world's best, high-performance, secure, and open source (key) digital currency trading system in Java. (Please ignore our awesomeness, because Niubi has a lot of boasting, and it may become really awesome over time)

     We hope to communicate with each other and improve together through the power of the open source community.

     Thank you very much for your help (issue or money. Okay, more money!), please feel free to submit a request or open an issue.

# Warning (make up FBI warning screen by yourself)

1. It is very difficult to operate an exchange.

    The Ztuo framework can make it easy for you to build a digital currency trading system, but it is far more difficult than building a website. Don't think that the simple one is to click next and the next step is complete. The entire system architecture is divided into many components, which require professional knowledge or a team to run successfully. Fortunately, if you have us, you can contact us at any time.

2. System security knowledge.

    The Ztuo framework cannot protect the security of your digital assets, nor can it guarantee the safe operation of your system. During the deployment process, you need to pay attention to the network security settings. If you are not good at it, you can find a professional operation and maintenance personnel.

3. Legal risks

- Legal Risk Article 1: Do not violate the laws and regulations of the People's Republic of China.
- Technology is not guilty, please use the Ztuo framework within the scope of the law.
- If you want to use Ztuo as a commercial application, it is best to hire a lawyer to ensure that your commercial application is within the scope permitted by law. The ztuo team is not responsible for any legal and economic issues caused by commercial projects.

4. Basic knowledge you need to know

- Legal knowledge (safety first, law most important)
- Java knowledge (mainly spring)
- Linux knowledge (CentOS, Ubuntu, etc.)
- safety knowledge

### Main technique

- Backend: Spring, SpringMVC, SpringData, SpringCloud, SpringBoot
- Database: Mysql, Mongodb
- Others: redis, kafka, Alibaba Cloud OSS, Tencent waterproof verification, ring letter push
- Front end: Vue, iView, less
- Provide both IOS and Android versions.

### test environment
- The test environment is for learning reference only! ! ! Please use it reasonably.

- Test environment address: 
- https://www.bizzan.top Account：13800138001 Password：123456
- https://admin.bizzan.top Account：test Password：123456
### join us
     In order to facilitate everyone to communicate and learn, please join the QQ exchange group:
     *Blockchain exchange technical knowledge exchange group [QQ group: 735446452]
### Open Source License Agreement
     Apache
---

# Overall structure

#### ![Topology-Image](/Topology-Image/143836_eac248e5_2182501.png)

# Logical architecture

#### ![Topology-Image](/Topology-Image/143856_66257325_2182501.png)

# Deployment architecture

#### ![Topology-Image](/Topology-Image/141710_07923003_2182501.png)

# Dependency

#### ![Topology-Image](/Topology-Image/194510_89803a9d_2182501.png)


#  Module introduction

1. ## cloud

- Provides SpringCloud microservice registry function, which is a basic module and must be deployed
- Dependent service: none

2. ## ucenter-api

- Provide user-related interfaces (such as login, registration, asset list). This module is based on the basic module and must be deployed
- Dependent services: mysql, kafka, redis, mongodb, SMS interface, email account

3. ## otc-api

- Provides an over-the-counter trading function interface, if there is no over-the-counter transaction, deployment is not required
- Dependent services: mysql, redis, mongodb, SMS interface

4. ## exchange-api

- Provide currency transaction interface, projects without currency transaction can not be deployed
- Dependent services: mysql, redis, mongodb, kafka

5. ## chat

- Provide real-time communication interface, basic module, need to be deployed
- Dependent services: mysql, redis, mongodb

6. ## admin

- Provide all service interfaces of the management background, which must be deployed
- Dependent services: mysql, redis, mongodb

7. ## wallet

- Provide wallet services such as depositing coins, withdrawing coins, and obtaining addresses. It is a basic module and must be deployed
- Dependent services: mysql, mongodb, kafka, cloud

8. ## market

- Provide interface services such as currency prices, k-line, real-time transactions, and over-the-counter transactions do not require deployment
- Dependent services: mysql, redis, mongodb, kafka, cloud

9. ## exchange

- Provide matching transaction services, OTC transactions do not need to be deployed
- Dependent services: mysql, mongodb, kafka

# Key business introduction

     The core modules of the back-end framework are exchange and market modules.

     Among them, the exhcnge module completely uses Java memory processing queues, which greatly speeds up the processing logic, does not involve database operations in the process, and ensures fast processing speed. After the project is started, it adopts the method of inheriting ApplicationListener and runs automatically;

     Unprocessed orders are automatically loaded after startup and reloaded into the JVM to ensure the accuracy of the data. After the order is processed by the exchange, the transaction record is sent to the market;

     The market module is mainly for database operations and persists user change information to the database. The main difficulty lies in interacting with the front-end socket push. Socket push adopts two methods. The web socket adopts SpringSocket and the mobile terminal adopts Netty push. The netty push is processed by timed tasks.

# Environment setup
-Centos 6.8
-MySQL 5.5.16
-Redis-x64-3.2.100
-Mongodb 3.6.13
-kafka_2.11-2.2.1
-nginx-1.16.0
-JRE 8u241
-JDK 1.8
-Vue
-Zookeeper


## Service deployment preparation

1. The project uses the Lombok plugin, no matter what IDE tool you use, please be sure to install the Lombok plugin first
2. QueryDsl is used in the project. If the class starting with Q is not found, please compile the corresponding core module, such as core, exchange-core, xxx-core.
3. The jar package that cannot be found is in the project jar folder
4. jdk version 1.8 or higher
5. Initialize the sql configuration file in the sql folder
The configuration file opens this setting will automatically create a table

## JPA
spring.jpa.hibernate.ddl-auto=update

## Modify service configuration file
Please modify the following configuration according to the actual deployment of the service. The location of the configuration file is as follows. If there is no configuration in the configuration file, it means that the module does not use this function and does not need to be added:

```
Each module/src/main/resources/dev/application.properties
```
mysql database:

```
spring.datasource.**
```
reids

```
redis.**
```
mongodb (mainly stores K-line chart related data)

```
spring.data.mongodb.uri
```
kafka

```
spring.kafka.bootstrap-servers
```
Alibaba Cloud OSS, image resource upload

```
aliyun.**
```
SMS configuration

```
sms.**
```
Mail authentication

```
spring.mail.**
```
Tencent waterproof check

```
water.proof.app.**
```

## Service start
 1. Maven build packaging service

 2. Upload the XX.jar in the target folder of each module to your server

 3. Start the cloud module first, then start the market and exchange modules, the rest is in no order.

 4. Start the service

    example:

    ```
    nohup java -jar /own jar package path/cloud.jar >/dev/null 2>&1 &
    ```
    
    ```
    nohup java -jar /web/app/cloud.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/exchange.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/market.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/exchange-api.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/ucenter-api.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/otc-api.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/chat.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/wallet.jar >/dev/null 2>&1 &
    nohup java -jar /web/app/admin.jar >/dev/null 2>&1 &
    ```
## Commercial demo
#### ![Topology-Image](/Topology-Image/01.png)
#### ![Topology-Image](/Topology-Image/02.png)
#### ![Topology-Image](/Topology-Image/03.png)
#### ![Topology-Image](/Topology-Image/04.png)
#### ![Topology-Image](/Topology-Image/05.png)
#### ![Topology-Image](/Topology-Image/06.png)
#### ![Topology-Image](/Topology-Image/07.png)
