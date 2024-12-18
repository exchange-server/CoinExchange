package com.bizzan.bitrade.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class kafkaPartitioner implements Partitioner {

    private static int random() {
        return (int) (Math.random() * 9) + 1;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 交易相关的随机分配，其他分配到0分区
        if (topic.startsWith("exchange")) {
            return random();
        } else {
            return 0;
        }
    }

    @Override
    public void close() {

    }
}
