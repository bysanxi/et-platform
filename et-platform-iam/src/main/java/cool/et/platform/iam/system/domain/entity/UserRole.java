package cool.et.platform.iam.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 角色分配
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user_role")
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserRole", description = "用户与角色绑定")
public class UserRole extends SuperEntity<Long> {

    @Schema(description = "角色ID")
    @TableField("role_id")
    private Long roleId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

}
