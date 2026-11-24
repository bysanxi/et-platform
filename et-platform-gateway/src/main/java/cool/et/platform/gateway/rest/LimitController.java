package cool.et.platform.gateway.rest;

import cn.hutool.core.util.IdUtil;
import cool.et.framework.commons.entity.Result;
import cool.et.platform.gateway.configuration.rule.LimitHelper;
import cool.et.platform.gateway.rest.domain.LimitRule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/rules/limits")
public class LimitController {
    
    private final LimitHelper limitHelper;
    
    @GetMapping
    public Result<Map<String, Object>> query() {
        Map<String, Object> data = new HashMap<>(5);
        final List<LimitRule> limitRules = limitHelper.query();
        data.put("total", limitRules.size());
        data.put("records", limitRules);
        data.put("current", 1);
        data.put("size", 20);
        data.put("pages", 1);
        return Result.success(data);
    }
    
    @PostMapping
    public Result<Void> add(@RequestBody LimitRule rule) {
        String uuid = IdUtil.fastSimpleUUID();
        rule.setId(uuid);
        limitHelper.saveOrUpdate(rule);
        return Result.success();
    }
    
    @PutMapping("/{id}")
    public Result<Void> edit(@PathVariable String id, @RequestBody LimitRule rule) {
        limitHelper.saveOrUpdate(rule);
        return Result.success();
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        limitHelper.delete(id);
        return Result.success();
    }
    
}
