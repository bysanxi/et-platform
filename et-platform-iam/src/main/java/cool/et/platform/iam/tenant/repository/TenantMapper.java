package cool.et.platform.iam.tenant.repository;

import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantMapper extends SuperMapper<Tenant> {
}
