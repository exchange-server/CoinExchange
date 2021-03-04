# CentOS 6.8/6.9 安装MongoDB

## 下载安装包

```
cd /web/bin
wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-amazon-3.6.4.tgz
tar zxvf mongodb-linux-x86_64-amazon-3.6.4.tgz
mv ./mongodb-linux-x86_64-amazon-3.6.4 ./mongodb
cd ./mongodb
```

## 安装准备

创建数据库文件夹与日志文件、配置文件：

```
mkdir ./data
touch ./mongod.log
touch ./mongodb.conf
```

mongodb.conf添加配置

```
dbpath=/mnt/mongodb/data/db
logpath=/mnt/mongodb/mongod.log
logappend=true
port=27017
fork=true
auth=true
journal=true
bind_ip=0.0.0.0
```



##  启停

如果在配置文件中配置好各项参数，则可以使用配置文件启动

```
 ./mongod --config /web/bin/mongodb/mongodb.conf
```

停止

```
./mongod --shutdown --dbpath=/web/bin/mongodb/data/
```

## 用户授权

mongodb安装好后没有任何用户，需要创建用户并授权

```
cd /web/bin/mongodb
./bin/mongo
use admin
```

添加管理用户（mongoDB 没有无敌用户root，只有能管理用户的用户 userAdminAnyDatabase）

```
db.createUser( {user: "admin",pwd: "123456",roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]})
```

注：添加完用户后可以使用show users或db.system.users.find()查看已有用户

退出exit，重新进入mongo shell，使用admin数据库并进行验证，如果不验证，是做不了任何操作的

```
./bin/mongo
 use admin
 db.auth("admin","123456")   #认证，返回1表示成功
```

验证之后还是做不了操作，因为admin只有用户管理权限，下面创建用户，用户都跟着库走

```
use bitrade
db.createUser({user: "root",pwd: "root",roles: [{ role: "readWrite", db: "bitrade" }]})
```

使用创建的用户root登录进行数据库操作

```
./bin/mongo
use bitrade
db.auth("root","root")   #认证，返回1表示成功
show collections
```

然后就可以进行增删改查各种数据操作。

