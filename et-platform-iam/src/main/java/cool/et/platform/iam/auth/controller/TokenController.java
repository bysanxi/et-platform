package cool.et.platform.iam.auth.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cool.et.framework.commons.exception.CheckedException;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.security.domain.UserInfoDetails;
import cool.et.platform.iam.auth.domain.dto.req.LoginReq;
import cool.et.platform.iam.auth.service.PasswordLoginService;
import cool.et.platform.iam.base.domain.dto.req.ChangePasswordReq;
import cool.et.platform.iam.base.domain.dto.req.ChangeUserInfoReq;
import cool.et.platform.iam.contract.account.LoginResp;
import cool.et.platform.iam.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
@Tag(name = "Token管理", description = "Token管理")
public class TokenController {

    private final AuthenticationContext context;
    private final UserService userService;
    private final PasswordLoginService passwordLoginService;

    @SaIgnore
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录")
    public LoginResp login(@Validated @RequestBody LoginReq req) {
        return passwordLoginService.login(req);
    }

    @GetMapping("/userinfo")
    @Operation(summary = "用户信息", description = "获取用户信息")
    public UserInfoDetails userinfo() {
        return (UserInfoDetails) context.getContext();
    }

    @GetMapping("/func_permissions")
    @Operation(summary = "用户信息", description = "获取用户信息")
    public List<String> funcPermissionList() {
        return context.funcPermissionList();
    }

    @PutMapping("/change_password")
    @Operation(summary = "修改密码")
    public void changePassword(@Validated @RequestBody ChangePasswordReq req) {
        if (!StringUtils.equals(req.getNewPassword(), req.getConfirmPassword())) {
            throw CheckedException.badRequest("新密码与确认密码不一致");
        }
        this.userService.changePassword(StpUtil.getLoginIdAsLong(), req.getCurrentPassword(), req.getNewPassword());
    }

    @PutMapping("/change_info")
    @Operation(summary = "信息修改")
    public void changeInfo(@Validated @RequestBody ChangeUserInfoReq req) {
        this.userService.changeInfo(req);
    }

    @SaIgnore
    @DeleteMapping("/logout")
    @Operation(summary = "退出登录")
    public void logout() {
        StpUtil.logout();
    }
}
