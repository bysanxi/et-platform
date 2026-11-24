package cool.et.platform.iam.tenant.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cool.et.framework.commons.annotation.log.AccessLog;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.framework.redis.plus.anontation.RedisLock;
import cool.et.framework.redis.plus.anontation.RedisParam;
import cool.et.platform.iam.tenant.domain.dto.req.TenantPageReq;
import cool.et.platform.iam.tenant.domain.dto.req.TenantSaveReq;
import cool.et.platform.iam.tenant.domain.dto.resp.TenantPageResp;
import cool.et.platform.iam.tenant.domain.entity.Tenant;
import cool.et.platform.iam.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tenants")
@Tag(name = "租户管理", description = "租户管理")
public class TenantController {

    private final TenantService tenantService;

    @Operation(summary = "查询可用", description = "按 ID 查询租户")
    @PostMapping("/ids")
    @SaCheckPermission("tenant:list")
    public List<Tenant> list(@RequestBody List<Long> ids) {
        return this.tenantService.list(Wraps.<Tenant>lbQ().in(Tenant::getId, ids));
    }

    @PostMapping("/page")
    @Operation(summary = "租户列表 - [Levin] - [DONE]")
    @SaCheckPermission(value = {"tenant:list"})
    public IPage<TenantPageResp> pageList(@RequestBody TenantPageReq req) {
        return tenantService.page(req.buildPage(), Wraps.<Tenant>lbQ()
                .like(Tenant::getName, req.getName())
                .eq(Tenant::getCode, req.getCode())
                .eq(Tenant::getStatus, req.getStatus())
                .eq(Tenant::getType, req.getType())).convert(x -> BeanUtil.toBean(x, TenantPageResp.class));
    }

    @PostMapping("/create")
    @AccessLog(module = "租户管理", description = "添加租户")
    @Operation(summary = "添加租户")
    @SaCheckPermission(value = {"tenant:add"})
    public void create(@Validated @RequestBody TenantSaveReq req) {
        tenantService.create(req);
    }

    @PutMapping("/{id}/modify")
    @AccessLog(module = "租户管理", description = "编辑租户")
    @Operation(summary = "编辑租户")
    @SaCheckPermission(value = {"tenant:edit"})
    public void modify(@PathVariable Long id, @Validated @RequestBody TenantSaveReq req) {
        tenantService.modify(id, req);
    }

    @PutMapping("/{id}/init-sql-script")
    @AccessLog(module = "", description = "加载初始数据")
    @Operation(summary = "加载初始数据")
    @SaCheckPermission("tenant:init-script")
    @RedisLock(prefix = "tenant:init-script")
    public void initSqlScript(@RedisParam(name = "id") @PathVariable Long id) {
        tenantService.initSqlScript(id);
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "租户管理", description = "删除租户")
    @Operation(summary = "删除租户")
    @SaCheckPermission(value = {"tenant:remove"})
    public void remove(@PathVariable Long id) {
        tenantService.removeById(id);
    }
}
