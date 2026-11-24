package cool.et.framework.redis.plus.lock;

import java.util.concurrent.TimeUnit;

/**
 * 锁信息（使用JDK21 Record特性，不可变且线程安全）
 *
 * @param key       锁的key
 * @param waitTime  等待时间
 * @param leaseTime 租约时间（-1表示启用看门狗自动续期）
 * @param timeUnit  时间单位
 * @param lockType  锁类型
 */
public record LockInfo(
        String key,
        long waitTime,
        long leaseTime,
        TimeUnit timeUnit,
        LockType lockType
) {
    /**
     * 默认等待时间（秒）
     */
    public static final long DEFAULT_WAIT_TIME = 3L;

    /**
     * 默认租约时间（-1表示启用看门狗）
     */
    public static final long DEFAULT_LEASE_TIME = -1L;

    /**
     * 创建默认配置的锁信息
     */
    public static LockInfo of(String key) {
        return new LockInfo(key, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, TimeUnit.SECONDS, LockType.REENTRANT);
    }

    /**
     * 创建指定等待时间的锁信息
     */
    public static LockInfo of(String key, long waitTime) {
        return new LockInfo(key, waitTime, DEFAULT_LEASE_TIME, TimeUnit.SECONDS, LockType.REENTRANT);
    }

    /**
     * 创建完整配置的锁信息
     */
    public static LockInfo of(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        return new LockInfo(key, waitTime, leaseTime, timeUnit, LockType.REENTRANT);
    }

    /**
     * 锁类型枚举
     */
    public enum LockType {
        /**
         * 可重入锁（默认）
         */
        REENTRANT,
        /**
         * 公平锁
         */
        FAIR,
        /**
         * 读锁
         */
        READ,
        /**
         * 写锁
         */
        WRITE,
        /**
         * 联锁（多把锁）
         */
        MULTI
    }
}
