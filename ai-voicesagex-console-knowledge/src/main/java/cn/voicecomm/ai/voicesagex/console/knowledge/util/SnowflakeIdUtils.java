package cn.voicecomm.ai.voicesagex.console.knowledge.util;

import cn.hutool.core.lang.Singleton;

/**
 * 雪花算法工具类
 */
public class SnowflakeIdUtils {
    // 机器id所占的位数
    private final long workerIdBits = 4L;

    // 数据标识id所占的位数
    private final long datacenterIdBits = 5L;

    // 最大机器ID，结果是31 (这个移位算法可以算出的结果)
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 最大数据标识id，结果是31 (这个移位算法可以算出的结果)
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // 序列在id中占的位数
    private final long sequenceBits = 10L;

    // 机器ID向左移12位
    private final long workerIdShift = sequenceBits;

    // 数据标识id向左移17位(12+5)
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间截向左移22位(5+5+12)
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 工作机器ID(0&#126;31)
    private long workerId;

    // 数据中心ID(0&#126;31)
    private long datacenterId;

    // 毫秒内序列(0&#126;4095)
    private long sequence = 0L;

    // 上次生成ID的时间截
    private long lastTimestamp = -1L;

    // 构造函数，传入工作机器ID和数据中心ID
    public SnowflakeIdUtils(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 雪花算法ID生成的主要方法
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - 1288834974657L) << timestampLeftShift) // 时间截位移
                | (datacenterId << datacenterIdShift) // 数据中心ID位移
                | (workerId << workerIdShift) // 机器ID位移
                | sequence; // 序列号
    }

    // 延迟到下一个毫秒，保证生成的ID的毫秒数不同
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    // 返回当前时间截（毫秒）
    protected long timeGen() {
        return System.currentTimeMillis();
    }
    public static Long getDefaultSnowFlakeId() {
        return ((SnowflakeIdUtils) Singleton.get(SnowflakeIdUtils.class, new Object[]{1L, 1L})).nextId();
    }
}
