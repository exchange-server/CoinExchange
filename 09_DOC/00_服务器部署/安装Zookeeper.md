# CentOS 6.8/6.9 安装 Zookeeper

## 下载安装

```
cd /data/install/apache
wget http://apache.fayea.com/zookeeper/zookeeper-3.4.10/zookeeper-3.4.10.tar.gz
tar -xzvf zookeeper-3.4.10.tar.gz
```

备用地址： https://apache.org/dist/zookeeper/stable/zookeeper-3.4.14.tar.gz

## 配置

建立数据目录

```
cd zookeeper-3.4.10
mkdir data
```

进入conf目录，将zoo_sample.cfg改为zoo.cfg文件

```
cd conf
mv zoo_sample.cfg zoo.cfg
```

## 启停

启动

```
/data/install/apache/zookeeper-3.4.10/bin/zkServer.sh start
```

停止

```
/data/install/apache/zookeeper-3.4.10/bin/zkServer.sh stop
```

