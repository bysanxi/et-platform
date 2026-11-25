package cool.et.platform.iam.contract.user;

import java.util.Map;
import java.util.Set;

/** 查询当前调用方可见的用户。 */
public interface UserApi {

    UserInfoResp findById(Long id);

    Map<Long, UserInfoResp> findByIds(Set<Long> ids);

    UserPage pageList(UserPageReq req);
}
