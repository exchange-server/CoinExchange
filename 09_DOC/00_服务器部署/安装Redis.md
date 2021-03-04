#  CentOS 6.8/6.9 安装 Redis 3.2

## 下载编译

```
$ wget http://download.redis.io/releases/redis-3.2.11.tar.gz
$ tar xzf redis-3.2.11.tar.gz
$ cd redis-3.2.11
$ make
```

 编译完成后，在Src目录下，有三个可执行文件redis-server、redis-benchmark、redis-cli，当前目录下有个配置文件redis.conf。然后拷贝到一个目录下。

```
mkdir /usr/redis
cp redis-server /usr/redis
cp redis-benchmark /usr/redis
cp redis-cli /usr/redis
cp redis.conf /usr/redis
cd /usr/redis
```

## 启动

```
$ ./redis-server redis.conf &
```

## 测试

然后用客户端测试一下是否启动成功。

```
$ ./redis-cli
redis> set foo bar
OK
redis> get foo
"bar"
```

## 设置密码

修改redis.conf文件：

修改 requirepass pass1234

## 设置远程访问

修改redis.conf文件：
注释  bind 127.0.0.1
修改  protected-mode no
