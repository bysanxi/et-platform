package cool.et.platform.iam.feign;

import cool.et.framework.commons.FeignConstants;
import cool.et.platform.iam.contract.user.UserApi;
import cool.et.platform.iam.contract.user.UserInfoResp;
import cool.et.platform.iam.contract.user.UserPage;
import cool.et.platform.iam.contract.user.UserPageReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

@FeignClient(name = FeignConstants.AUTH_FEIGN_NAME, dismiss404 = true)
public interface UserFeign extends UserApi {

    @Override
    @PostMapping("/users/batch_ids")
    Map<Long, UserInfoResp> findByIds(@RequestBody Set<Long> ids);

    @Override
    @PostMapping("/users/page")
    UserPage pageList(@RequestBody UserPageReq req);

    @Override
    @GetMapping("/users/{id}")
    UserInfoResp findById(@PathVariable("id") Long id);
}
