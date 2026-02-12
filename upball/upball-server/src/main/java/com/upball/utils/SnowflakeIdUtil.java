package com.upball.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花算法ID生成器（简化版）
 */
@Component
public class SnowflakeIdUtil {

    // 起始时间戳 (2024-01-01)
    private final long START_TIMESTAMP = 1704067200000L;

    // 各部分位数
    private final long DATA_CENTER_ID_BITS = 5L;
    private final long MACHINE_ID_BITS = 5L;
    private final long SEQUENCE_BITS = 12L;

    // 最大值
    private final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    private final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 位移
    private final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    private final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATA_CENTER_ID_BITS;

    private long dataCenterId = 1L;
    private long machineId = 1L;
    private AtomicLong sequence = new AtomicLong(0L);
    private volatile long lastTimestamp = -1L;

    /**
     * 生成ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨");
        }

        if (timestamp == lastTimestamp) {
            long seq = sequence.incrementAndGet() & MAX_SEQUENCE;
            if (seq == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence.set(0L);
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence.get();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
