package cool.et.framework.coordination.leadership.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("extend.coordination.redis")
public class LeadershipProperties {
    /** Redis 节点列表，格式为 host:port，多个地址以逗号分隔。 */
    private List<String> servers = List.of();
    private String mode = "single";
    private String password;
    private String master;
    private long leadershipTtlInMilliseconds = 10000;
}
