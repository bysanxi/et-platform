package cool.et.framework.boot.remote.dict;

import cn.hutool.core.lang.Pair;
import cool.et.framework.commons.remote.LoadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class DictLoadService implements LoadService<Object> {

    private static final String PLAT_DICT_HASH_KEY = "dict:platform";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<Object, Object> findByIds(Set<Object> ids) {
        log.info("字典服务......");
        throw new RuntimeException("暂未实现");
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Object, Object> findByIds(String tag) {
        return (Map<Object, Object>) redisTemplate.opsForHash().get(PLAT_DICT_HASH_KEY, tag);
    }

    @Override
    public void refreshCache(Map<String, List<Pair<String, String>>> data) {
        if (data == null) {
            return;
        }
        for (Map.Entry<String, List<Pair<String, String>>> entry : data.entrySet()) {
            String key = entry.getKey();
            List<Pair<String, String>> value = entry.getValue();
            if (value == null) {
                continue;
            }
            Map<String, String> map = value.stream().collect(toMap(Pair::getKey, Pair::getValue));
            redisTemplate.opsForHash().put(PLAT_DICT_HASH_KEY, key, map);
        }
    }
}
