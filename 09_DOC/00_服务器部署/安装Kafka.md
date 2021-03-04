# CentOS 6.8/6.9 安装 Kafka

安装kafka之前必须先安装zookeeper。

## 下载安装

```
wget http://mirrors.shu.edu.cn/apache/kafka/1.0.1/kafka_2.12-1.0.1.tgz
tar -zxvf kafka_2.12-1.0.1.tgz
cd kafka_2.12-1.0.1
```

## 修改配置

修改conf目录下的server.properties

#必须是唯一的
broker.id=0

#端口号
port=9092

listeners=PLAINTEXT://{本机内网IP}:9092

advertised.listeners=PLAINTEXT://{本机外网IP}:9092

num.network.threads=3

num.io.threads=8

socket.send.buffer.bytes=102400

socket.receive.buffer.bytes=102400

socket.request.max.bytes=104857600

#log路径，建议修改，否则重启就没有了
log.dirs=/tmp/kafka-logs

num.partitions=1

num.recovery.threads.per.data.dir=1

log.retention.hours=168

log.segment.bytes=1073741824

log.retention.check.interval.ms=300000

zookeeper.connect=localhost:2181

zookeeper.connection.timeout.ms=6000

## 启停

启动

```
bin/zookeeper-server-start.sh config/zookeeper.properties &
bin/kafka-server-start.sh config/server.properties &
```

## 验证

创建生产者

```
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
```

创建消费者

```
bin/kafka-console-consumer.sh --bootstrap-server 192.168.8.9:9092 --topic test
```


