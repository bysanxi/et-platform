package cool.et.platform.iam.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cool.et.framework.boot.log.AccessLogInfo;
import cool.et.framework.commons.security.AuthenticationContext;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.base.domain.dto.req.OptLogPageReq;
import cool.et.platform.iam.base.domain.entity.OptLog;
import cool.et.platform.iam.base.service.OptLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 操作日志
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/opt-logs")
@Tag(name = "操作日志", description = "操作日志")
public class OptLogController {
    
    private final OptLogService optLogService;
    private final AuthenticationContext context;
    
    @GetMapping
    @Operation(summary = "查询日志 - [DONE] - [Levin]", description = "查询日志 - [DONE] - [Levin]")
    @SaCheckPermission(value = {"monitor:log:opt"})
    public Page<OptLog> page(OptLogPageReq req) {
        return this.optLogService.page(req.buildPage(), Wraps.<OptLog>lbQ()
                .eq(OptLog::getHttpMethod, req.getHttpMethod())
                .eq(OptLog::getStatus, req.getStatus())
                .eq(OptLog::getPlatform, req.getPlatform()));
    }
    
    @PostMapping("/listener")
    @Operation(summary = "监听日志 - [DONE] - [Levin]", description = "监听日志 - [DONE] - [Levin]")
    public void listener(@RequestBody AccessLogInfo info) {
        info.setTenantId(context.tenantId());
        info.setTenantCode(context.tenantCode());
        info.setCreateBy(context.userId());
        info.setCreateName(context.nickname());
        this.optLogService.listener(info);
    }
    
}
