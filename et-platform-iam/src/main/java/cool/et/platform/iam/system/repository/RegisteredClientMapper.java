package cool.et.platform.iam.system.repository;

import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.auth.domain.entity.RegisteredClient;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredClientMapper extends SuperMapper<RegisteredClient> {
}
