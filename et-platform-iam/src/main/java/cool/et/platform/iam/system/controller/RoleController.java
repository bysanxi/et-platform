package cool.et.platform.iam.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cool.et.framework.commons.annotation.log.AccessLog;
import cool.et.framework.db.mybatisplus.wrap.Wraps;
import cool.et.platform.iam.system.domain.dto.req.RolePageReq;
import cool.et.platform.iam.system.domain.dto.req.RoleResSaveReq;
import cool.et.platform.iam.system.domain.dto.req.RoleSaveReq;
import cool.et.platform.iam.system.domain.dto.req.RoleUserSaveReq;
import cool.et.platform.iam.system.domain.dto.resp.RoleDetailResp;
import cool.et.platform.iam.system.domain.dto.resp.RolePageResp;
import cool.et.platform.iam.system.domain.dto.resp.RolePermissionResp;
import cool.et.platform.iam.system.domain.dto.resp.UserRoleResp;
import cool.et.platform.iam.system.domain.entity.Role;
import cool.et.platform.iam.system.domain.entity.RoleRes;
import cool.et.platform.iam.system.service.RoleResService;
import cool.et.platform.iam.system.service.RoleService;
import cool.et.platform.iam.system.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理")
public class RoleController {

    private final RoleService roleService;
    private final RoleResService roleResService;
    private final UserRoleService userRoleService;

    @PostMapping("/page")
    @Parameters({
            @Parameter(description = "名称", name = "name", in = ParameterIn.QUERY),
    })
    @Operation(summary = "角色列表 - [Levin] - [DONE]")
    @SaCheckPermission(value = {"sys:role:page"})
    public IPage<RolePageResp> pageList(@RequestBody RolePageReq req) {
        return this.roleService.page(req.buildPage(), Wraps.<Role>lbQ().like(Role::getName, req.getName())
                        .eq(Role::getStatus, req.getStatus()).eq(Role::getScopeType, req.getScopeType()))
                .convert(x -> BeanUtil.toBean(x, RolePageResp.class));
    }

    @SaCheckPermission("sys:role:page")
    @GetMapping("/{id}/detail")
    @Operation(summary = "角色详情")
    public RoleDetailResp detail(@PathVariable Long id) {
        return roleService.detail(id);
    }

    @PostMapping("/create")
    @AccessLog(module = "角色管理", description = "添加角色")
    @Operation(summary = "添加角色")
    @SaCheckPermission(value = {"sys:role:add"})
    public void create(@Validated @RequestBody RoleSaveReq req) {
        roleService.create(req);
    }

    @PutMapping("/{id}")
    @AccessLog(module = "角色管理", description = "编辑角色")
    @Operation(summary = "编辑角色")
    @SaCheckPermission(value = {"sys:role:edit"})
    public void modify(@PathVariable Long id, @Validated @RequestBody RoleSaveReq req) {
        roleService.modify(id, req);
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "角色管理", description = "删除角色")
    @Operation(summary = "删除角色")
    @SaCheckPermission(value = {"sys:role:remove"})
    public void remove(@PathVariable Long id) {
        this.roleService.removeByRoleId(id);
    }

    @Operation(summary = "角色关联的用户")
    @SaCheckPermission("sys:role:page")
    @GetMapping("/{roleId}/users")
    public UserRoleResp userByRoleId(@PathVariable Long roleId) {
        roleService.detail(roleId);
        return userRoleService.findUserByRoleId(roleId);
    }

    @SaCheckPermission("sys:role:page")
    @GetMapping("/{role_id}/permissions")
    @Operation(summary = "资源权限", description = "只能看到自身权限")
    public RolePermissionResp permission(@PathVariable("role_id") Long roleId) {
        roleService.detail(roleId);
        return this.roleService.findRolePermissionById(roleId);
    }

    @Operation(summary = "角色关联的资源")
    @SaCheckPermission("sys:role:page")
    @GetMapping("/{roleId}/role_res")
    public List<RoleRes> resByRoleId(@PathVariable Long roleId) {
        roleService.detail(roleId);
        return roleResService.list(Wraps.<RoleRes>lbQ().eq(RoleRes::getRoleId, roleId));
    }

    @AccessLog(module = "角色管理", description = "分配资源")
    @Operation(summary = "分配资源")
    @PutMapping("/{roleId}/assign-resources")
    @SaCheckPermission(value = {"sys:role:assign-resource"})
    public void assignResource(@PathVariable Long roleId, @RequestBody RoleResSaveReq req) {
        req.setRoleId(roleId);
        this.roleResService.assignResource(req);

    }

    @AccessLog(module = "角色管理", description = "分配用户")
    @Operation(summary = "分配用户")
    @PutMapping("/{roleId}/assign-users")
    @SaCheckPermission(value = {"sys:role:assign-users"})
    public void assignUser(@PathVariable Long roleId, @RequestBody RoleUserSaveReq req) {
        this.roleService.assignUser(roleId, req.getUserIdList());
    }

}
