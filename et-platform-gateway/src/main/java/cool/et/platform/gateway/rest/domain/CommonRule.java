package cool.et.platform.gateway.rest.domain;

import lombok.Data;

import java.time.Instant;

@Data
public class CommonRule {
    
    private String id;
    private String method;
    private String path;
    private Boolean status;
    private Instant startTime;
    private Instant endTime;
    
}
