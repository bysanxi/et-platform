package cool.et.platform.iam.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import cool.et.framework.commons.BeanUtilPlus;
import cool.et.framework.commons.MapHelper;
import cool.et.framework.commons.annotation.log.AccessLog;
import cool.et.framework.commons.security.DataPermission;
import cool.et.framework.db.mybatisplus.datascope.service.DataScopeService;
import cool.et.framework.commons.entity.Entity;
import cool.et.platform.iam.system.domain.dto.req.UserPageReq;
import cool.et.platform.iam.system.domain.dto.req.UserSaveReq;
import cool.et.platform.iam.system.domain.dto.req.UserUpdateReq;
import cool.et.platform.iam.system.domain.dto.resp.UserPageResp;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户管理
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户管理")
public class UserController {
    
    private final UserService userService;
    private final DataScopeService dataScopeService;
    
    @PostMapping("/page")
    @Operation(summary = "用户列表 - [Levin] - [DONE]")
    @SaCheckPermission(value = {"sys:user:page"})
    public IPage<UserPageResp> pageList(@RequestBody UserPageReq req) {
        return this.userService.pageList(req);
    }
    
    @AccessLog(module = "用户管理", description = "重置密码", response = false)
    @PutMapping("/{id}/reset-password")
    @SaCheckPermission(value = {"sys:user:reset"})
    @Operation(summary = "重置密码", description = "重置密码,并且将随机生成的密码通过邮箱/短信的形式发送")
    public Map<String, String> resetPassword(@PathVariable Long id) {
        return Map.of("temporaryPassword", this.userService.resetPassword(id));
    }
    
    @PostMapping("/create")
    @AccessLog(module = "用户管理", description = "添加用户")
    @Operation(summary = "添加用户")
    @SaCheckPermission(value = {"sys:user:add"})
    public void create(@Validated @RequestBody UserSaveReq req) {
        this.userService.create(req);
    }

    @PutMapping("/{id}")
    @AccessLog(module = "用户管理", description = "编辑用户")
    @Operation(summary = "编辑用户")
    @SaCheckPermission(value = {"sys:user:edit"})
    public void modify(@PathVariable Long id, @Validated(Entity.Update.class) @RequestBody UserUpdateReq req) {
        this.userService.modify(id, req);
    }

    @DeleteMapping("/{id}")
    @AccessLog(module = "用户管理", description = "删除用户")
    @Operation(summary = "删除用户")
    @SaCheckPermission(value = {"sys:user:remove"})
    public void del(@PathVariable Long id) {
        this.userService.delete(id);
    }
    
    @PostMapping("/ids")
    @Operation(summary = "ID批量查询")
    public List<UserPageResp> idList(@RequestBody Set<Long> ids) {
        return BeanUtilPlus.toBeans(this.userService.visibleUsersByIds(ids), UserPageResp.class);
    }
    
    @PostMapping("/batch_ids")
    @Operation(summary = "ID批量查询")
    public Map<Long, UserPageResp> batchIds(@RequestBody Set<Long> ids) {
        final List<User> users = this.userService.visibleUsersByIds(ids);
        return MapHelper.toHashMap(users, Entity::getId, x -> BeanUtil.toBean(x, UserPageResp.class));
    }

    @GetMapping("/{id}")
    @Operation(summary = "用户信息查询")
    public UserPageResp getUser(@PathVariable Long id) {
        final User user = this.userService.requireVisibleUser(id);
        return BeanUtil.toBean(user, UserPageResp.class);
    }

    @GetMapping("/{id}/data_permission")
    @SaCheckPermission("sys:user:page")
    @Operation(summary = "获取租户内用户的数据权限")
    public DataPermission dataPermission(@PathVariable Long id) {
        User user = userService.requireVisibleUser(id);
        return dataScopeService.getDataScopeById(user.getId(), user.getOrgId());
    }
}
