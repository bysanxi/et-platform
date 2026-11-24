package cool.et.framework.redis.plus.sequence;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public record RedisSequenceHelper(StringRedisTemplate redisTemplate) {

    public String generate(Sequence sequence) {
        DateTimeFormatter formatter = sequence.formatter();
        String localDate = "";
        if (formatter != null) {
            localDate = LocalDateTime.now().format(formatter);
        }
        Long increment = redisTemplate.opsForValue().increment(StrUtil.join(sequence.key(), localDate));
        return sequence.prefix()
                + localDate
                + StrUtil.blankToDefault(sequence.delimiter(), "")
                + StrUtil.padPre(increment + "", sequence.size(), '0');
    }

    /**
     * 按规则生成流水号
     *
     * @param sequence     规则
     * @param isolationKey 隔离（比如 租户ID）
     * @return 流水号 = 前缀 + 日期时间 + 自增流水号
     */
    public String generate(Sequence sequence, Object isolationKey) {
        DateTimeFormatter formatter = sequence.formatter();
        String localDate = "";
        if (formatter != null) {
            localDate = LocalDateTime.now().format(formatter);
        }
        Long increment = redisTemplate.opsForHash().increment(sequence.key() + localDate, String.valueOf(isolationKey), 1);
        return sequence.prefix()
                + localDate
                + StrUtil.blankToDefault(sequence.delimiter(), "")
                + StrUtil.padPre(increment + "", sequence.size(), '0');
    }

}
