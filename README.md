数字货币合约期权交易所解决方案
- 加入交流群：区块链技术交流群【QQ群：530862680】【QQ:247818019】
- E-mail:247818019@qq.com
- Telegram: https://t.me/bizzancom
本源码仅限于交流学习，凡涉及到法律问题与本人无关

=====================================
- ## [English](README-EN.md)
---
### 提问和建议
- 使用Issuse，我们会及时跟进解答。
- 加入交流群：区块链技术交流群【QQ群：530862680】【QQ:247818019】
- E-mail:247818019@qq.com
- Telegram: https://t.me/bizzancom

# 捐赠：
#### 您的捐赠是我们开源最大的动力
- ETH/USDT(以太坊/USDT)：0xE4Be628D596995f2eB91E78392aD4976ec6BEaa1

#### ZTuo是一个免费的、开源的数字货币交易系统，她使用目前最流行的Java框架和相关技术开发而成。

## 愿景
    我们的使命是用Java开发世界上最好的、高性能的、安全的、开源的（重点）数字货币交易系统。（请忽略我们的牛逼，因为牛逼吹得多了，可能慢慢就真的牛逼了）

    我们希望通过开源社区的力量能相互交流，共同改进。

    非常感谢您的帮助（issue  or money。好吧，more money！），请随时提交请求或公开问题。

## 警告(自行脑补FBI warning 画面)

1. 运营一家交易所是非常不容易的.

    Ztuo 框架可以使你很容易的建立一套数字货币交易系统，但是，她远远比搭建一个网站要难的多得多.不要以为简单的就是点击下一步，下一步即可完成。整个体系架构分为了很多的组件，需要专业的知识或者团队才能运行成功，好在有我们，可以随时联系我们。

2. 系统安全知识.

    Ztuo 框架不能保护你的数字资产安全，也不能保证你的系统运行安全。在部署过程中，需要注意网络安全的设置，如果你不在行的话，可以找一个专业的运维人员。

3. 法律风险

- 法律风险第一条：不要触犯中华人民共和国的法律条例。
- 技术无罪，请在法律范围内使用Ztuo框架。
- 如果你想使用Ztuo作为商业应用，最好请个律师，确保你的商业应用在法律允许的范围内。一切用于商业化项目所带来的法律和经济问题，ztuo团队概不负责。

4. 你需要知道的基本知识

- 法律知识（安全第一条，法律最重要）
- Java知识（主要是spring）
- linux知识（CentOS、Ubuntu等等）
- 安全知识

### 主要技术

- 后端：Spring、SpringMVC、SpringData、SpringCloud、SpringBoot
- 数据库：Mysql、Mongodb
- 其他：redis、kafka、阿里云OSS、腾讯防水校验、环信推送
- 前端：Vue、iView、less

### 测试环境
- 测试环境仅供学习参考！！！请各位学习的小伙伴合理运用。

- 测试环境地址 : 
- https://www.bizzan.top 账户：13800138001 密码：123456
- https://admin.bizzan.top 账户：test 密码：123456

### 加入我们
    为方便大家交流和学习，请各位小伙伴加入QQ交流群：
	【QQ群：530862680】【QQ:247818019】
### 开源许可协议
    Apache

---

# 整体架构

#### ![Topology-Image](/Topology-Image/143836_eac248e5_2182501.png)

# 逻辑架构

#### ![Topology-Image](/Topology-Image/143856_66257325_2182501.png)

# 部署架构

#### ![Topology-Image](/Topology-Image/141710_07923003_2182501.png)

# 依赖关系

#### ![Topology-Image](/Topology-Image/194510_89803a9d_2182501.png)

##  模块介绍

1. cloud

- 提供SpringCloud微服务注册中心功能，为基础模块，必须部署
- 依赖服务：无

2. ucenter-api

- 提供用户相关的接口（如登录、注册、资产列表）,该模块为基础为基础模块，必须部署
- 依赖服务：mysql,kafka,redis,mongodb,短信接口，邮箱账号

3. otc-api

- 提供场外交易功能接口，没有场外交易的可以不部署
- 依赖服务：mysql,redis,mongodb,短信接口

4. exchange-api

- 提供币币交易接口，没有币币交易的项目可以不部署
- 依赖服务：mysql,redis,mongodb,kafka

5. chat

- 提供实时通讯接口，基础模块，需要部署
- 依赖服务：mysql,redis,mongodb

6. admin

- 提供管理后台的所有服务接口，必须部署
- 依赖服务：mysql,redis,mongodb

7. wallet

- 提供充币、提币、获取地址等钱包服务，为基础模块，必须部署
- 依赖服务：mysql,mongodb,kafka,cloud

8. market

- 提供币种价格、k线、实时成交等接口服务，场外交易不需要部署
- 依赖服务：mysql,redis,mongodb,kafka,cloud

9. exchange

- 提供撮合交易服务，场外交易不需要部署
- 依赖服务：mysql,mongodb,kafka

##  重点业务介绍

    后端框架的核心模块为 exchange,market模块。

    其中exhcnge模块完全采用Java内存处理队列,大大加快处理逻辑,中间不牵涉数据库操作,保证处理速度快,其中项目启动后采用继承ApplicationListener方式，自动运行；

    启动后自动加载未处理的订单,重新加载到JVM中，从而保证数据的准确，exchange将订单处理后，将成交记录发送到market;

    market模块主要都是数据库操作，将用户变化信息持久化到数据库中。主要难点在于和前端交互socket推送，socket推送采用两种方式，web端socket采用SpringSocket，移动端采用Netty推送,其中netty推送通过定时任务处理。

## 环境搭建
- Centos 6.8
- MySQL 5.5.16
- Redis-x64-3.2.100
- Mongodb 3.6.13
- kafka_2.11-2.2.1
- nginx-1.16.0
- JRE 8u241
- JDK 1.8
- Vue
- Zookeeper


## 服务部署准备

1. 项目用了Lombok插件，无论用什么IDE工具，请务必先安装Lombok插件
2. 项目用了QueryDsl，如果遇见以Q开头的类找不到，请先编译一下对应的core模块，例如core、exchange-core、xxx-core这种模块
3. 找不到的jar包在项目jar文件夹下
4. jdk版本1.8以上
5. 初始化sql在sql文件夹中配置文件
配置文件打开这个设置会自动建表
#jpa
spring.jpa.hibernate.ddl-auto=update

## 修改服务配置文件
请根据服务实际部署情况修改以下配置。配置文件位置如下，如果配置文件中没有某一项配置，说明该模块未使用到该项功能，无需添加：

```
各个模块/src/main/resources/dev/application.properties
```

mysql数据库:

```
spring.datasource.**
```

reids

```
redis.**
```

mongodb(主要存储K线图相关数据)

```
spring.data.mongodb.uri
```

kafka

```
spring.kafka.bootstrap-servers
```

阿里云OSS，图片资源上传

```
aliyun.**
```

短信配置

```
sms.**
```

邮件认证

```
spring.mail.**
```

腾讯防水校验

```
water.proof.app.**
```

### 服务启动
 1. maven构建打包服务

 2. 将各个模块target文件夹下的XX.jar上传到自己的服务器

 3. 先启动cloud模块，再启动market，exchange模块，剩下的没有顺序。

 4. 启动服务

    例：

    ```
    nohup  java  -jar  /自己的jar包路径/cloud.jar  >/dev/null 2>&1 &
    ```
    
    ```
    nohup  java  -jar  /web/app/cloud.jar  >/dev/null 2>&1 &
    nohup  java  -jar  /web/app/exchange.jar  >/dev/null 2>&1 &
    nohup  java  -jar  /web/app/market.jar  >/dev/null 2>&1 &
    nohup  java  -jar  /web/app/exchange-api.jar  >/dev/null 2>&1 &
    nohup  java  -jar  /web/app/ucenter-api.jar  >/dev/null 2>&1 &
    nohup  java  -jar  /web/app/otc-api.jar  >/dev/null 2>&1 & 
    nohup  java  -jar  /web/app/chat.jar  >/dev/null 2>&1 & 
    nohup  java  -jar  /web/app/wallet.jar  >/dev/null 2>&1 & 
    nohup  java  -jar  /web/app/admin.jar  >/dev/null 2>&1 &
    ```

## 商业版演示图
#### ![Topology-Image](/Topology-Image/01.png)
#### ![Topology-Image](/Topology-Image/02.png)
#### ![Topology-Image](/Topology-Image/03.png)
#### ![Topology-Image](/Topology-Image/04.png)
#### ![Topology-Image](/Topology-Image/05.png)
#### ![Topology-Image](/Topology-Image/06.png)
#### ![Topology-Image](/Topology-Image/07.png)
