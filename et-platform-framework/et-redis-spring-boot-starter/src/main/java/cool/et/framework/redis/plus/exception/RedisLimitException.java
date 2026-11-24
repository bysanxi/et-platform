package cool.et.framework.redis.plus.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Redis 限流异常
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RedisLimitException extends RuntimeException {

    private int code;

    public RedisLimitException(String message) {
        super(message);
        this.code = 429;
    }

    public RedisLimitException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RedisLimitException(String message, Throwable cause) {
        super(message, cause);
        this.code = 429;
    }

    public RedisLimitException(Throwable cause) {
        super(cause);
        this.code = 429;
    }

    public RedisLimitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = 429;
    }
}
