package cool.et.platform.iam.auth.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.et.framework.db.mybatisplus.datascope.core.DataScope;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.auth.domain.entity.LoginLog;
import cool.et.platform.iam.auth.service.LoginLogService;
import cool.et.platform.iam.base.domain.dto.req.LoginLogPageReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录日志
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/login_logs")
@Tag(name = "登录日志", description = "登录日志")
@RequiredArgsConstructor
public class LoginLogController {
    
    private final LoginLogService loginLogService;
    
    @GetMapping
    @Operation(summary = "查询日志 - [DONE] - [Levin]", description = "查询日志 - [DONE] - [Levin]")
    @SaCheckPermission(value = {"monitor:log:login"})
    public Page<LoginLog> pageList(LoginLogPageReq req) {
        return DataScope.run(() -> loginLogService.page(req.buildPage(), Wraps.<LoginLog>lbQ()
                .like(LoginLog::getCreateBy, req.getNickname())
                .like(LoginLog::getPrincipal, req.getPrincipal())
                .eq(LoginLog::getPlatform, req.getPlatform())));
    }
    
}
