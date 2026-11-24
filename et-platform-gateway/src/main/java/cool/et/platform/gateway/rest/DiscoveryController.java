package cool.et.platform.gateway.rest;

import cool.et.framework.commons.entity.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/gateway/discoveries")
public class DiscoveryController {
    
    private final DiscoveryClient discoveryClient;
    
    @GetMapping
    public List<String> serviceUrl() {
        return discoveryClient.getServices();
    }
    
    @GetMapping("/dict")
    public Result<List<Map<String, Object>>> dict() {
        final List<String> services = discoveryClient.getServices();
        return Result.success(services.stream().map(serviceId -> {
            Map<String, Object> object = new HashMap<>();
            object.put("serviceId", serviceId);
            return object;
        }).collect(toList()));
    }
    
}
