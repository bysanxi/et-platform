package cool.et.platform.iam.auth.controller;

import cn.dev33.satoken.same.SaSameUtil;
import cool.et.platform.iam.auth.service.UserAccountService;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.contract.account.UserAccountApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserAccountController implements UserAccountApi {
    private final UserAccountService accounts;

    @Override
    @PostMapping("/users/lookup")
    public Account lookup(@RequestBody Lookup request) {
        SaSameUtil.checkCurrentRequestToken();
        return accounts.lookup(request);
    }

    @Override
    @PostMapping("/users/register")
    public Registered register(@RequestBody Registration request) {
        SaSameUtil.checkCurrentRequestToken();
        return accounts.register(request);
    }

    @Override
    @PostMapping("/users/{userId}/password-reset")
    public Registered resetPassword(@PathVariable("userId") Long userId, @RequestBody PasswordReset request) {
        SaSameUtil.checkCurrentRequestToken();
        return accounts.resetPassword(userId, request);
    }

    @Override
    @PostMapping("/token/mobile-login")
    public LoginResp mobileLogin(@RequestBody MobileLogin request) {
        SaSameUtil.checkCurrentRequestToken();
        return accounts.mobileLogin(request);
    }
}
