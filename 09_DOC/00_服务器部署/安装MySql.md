# Centos6.8/6.9 通过yum 安装Mysql

### 下载YUM 源文件

官方下载址：http://dev.mysql.com/downloads/repo/yum/
以上地址中可以下载各种版本的mysql，为了方便这里准备了5.6的私有下载地址

5.6下载地址：http://file.wxmarket.cn/mysql-community-release-el6-5.noarch.rpm

可以通过以下命令下载

```
wget http://file.wxmarket.cn/mysql-community-release-el6-5.noarch.rpm
```

### 安装源地址

 找到步骤1下载的RPM文件

```
$ rpm -Uvh mysql-community-release-el6-5.noarch.rpm
```

### 安装mysql

以上步骤执行成功后，yum 源有就有mysql 6.5的安装信息，直接执行安装命令即可

```
$ yum install mysql-community-server
```


yum 会自动安装mysql 所需要的依赖，坐等就行啦~

### 启动mysql

安装成功后，可以通过 service 命令管理 mysqld
(如果缺少my.cnf文件，需要找一个可用的)

```
$ service mysqld start
```

### 安全设置

建议运行安全设置脚本，用于设置mysql的安全信息(包括设置root密码)

```
$ mysql_secure_installation
```

不允许匿名登陆,允许远程连接
OK,搞定
相比源码安装，以上方法的优势显而易见，这也是mysql官方推荐的安装方法，当然只适用于centos,federa 等用yum 管理工具的系统。

### 初始化数据库

```
mysql > create database if not exists bitrade default character set = 'utf8';
```

```
执行项目自带的sql脚本
```

```
mysql > INSERT INTO `admin` VALUES (1,'1' '1','XXXXXX@qq.com',  '0',now(), '0:0:0:0:0:0:0:1','0','', now(), '18000000000', '47943eeeab5ed28f8a93f7fb77ec5111', '312', '人人', '1', '0', 'root', '1');
```

