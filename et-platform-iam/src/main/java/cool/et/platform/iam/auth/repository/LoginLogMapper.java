package cool.et.platform.iam.auth.repository;

import cool.et.framework.db.mybatisplus.ext.SuperMapper;
import cool.et.platform.iam.auth.domain.entity.LoginLog;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 系统日志
 * </p>
 *
 * @since 2019-10-20
 */

@Repository
public interface LoginLogMapper extends SuperMapper<LoginLog> {
    
    /**
     * 统计 IP 数据
     *
     * @return 统计结果
     */
    @Select("SELECT count(DISTINCT ( ip )) FROM c_login_log")
    long countDistinctLoginIp();
    
}
