## 关于

00_Framework下包含了后端项目的集合，本项目基于SpringCloud开发，具体依赖关系如下图所示：
![依赖关系](https://images.gitee.com/uploads/images/2020/0408/155903_9173b5dc_2182501.png "QQ截图20200407194419.png")


## 平台使用的技术

- 后端：Spring、SpringMVC、SpringData、SpringCloud、SpringBoot
- 数据库：Mysql、Mongodb
- 其他：redis、kafka、阿里云OSS、腾讯防水校验
- 前端：Vue、iView、less

## 注意事项

- 项目用了Lombok插件，无论用什么IDE工具，请务必先安装Lombok插件
- 项目用了QueryDsl，如果遇见以Q开头的类找不到，请先编译一下对应的core模块，例如core、exchange-core、xxx-core这种模块
- 找不到的jar包在项目jar文件夹下
- jdk版本1.8以上
- 初始化sql在sql文件夹中配置文件 配置文件打开这个设置会自动建表 #jpa spring.jpa.hibernate.ddl-auto=update

## 配置文件需要修改的地方

- msyql数据库配置
- redis缓存数据库配置
- mongodb(主要存储K线图相关数据)
- kafka配置
- 阿里云OSS，图片资源上传
- 短信配置
- 邮件认证