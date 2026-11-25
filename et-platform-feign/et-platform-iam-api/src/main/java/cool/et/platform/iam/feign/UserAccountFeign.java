package cool.et.platform.iam.feign;

import cool.et.framework.commons.FeignConstants;
import cool.et.platform.iam.configure.IamServiceFeignConfiguration;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.contract.account.UserAccountApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = FeignConstants.AUTH_FEIGN_NAME, contextId = "userAccount",
        configuration = IamServiceFeignConfiguration.class)
public interface UserAccountFeign extends UserAccountApi {

    @Override
    @PostMapping("/users/lookup")
    Account lookup(@RequestBody Lookup request);

    @Override
    @PostMapping("/users/register")
    Registered register(@RequestBody Registration request);

    @Override
    @PostMapping("/users/{userId}/password-reset")
    Registered resetPassword(@PathVariable("userId") Long userId, @RequestBody PasswordReset request);

    @Override
    @PostMapping("/token/mobile-login")
    LoginResp mobileLogin(@RequestBody MobileLogin request);
}
