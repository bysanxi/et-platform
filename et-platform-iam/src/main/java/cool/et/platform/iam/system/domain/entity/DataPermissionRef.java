package cool.et.platform.iam.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.commons.entity.Entity;
import cool.et.framework.commons.security.DataResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 数据权限关联表
 * 支持多维度、多拥有者类型的数据权限配置
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_data_permission_ref")
@EqualsAndHashCode(callSuper = true)
public class DataPermissionRef extends Entity<Long> {
    
    @Schema(description = "所有者ID（角色ID/用户ID等）")
    @TableField("owner_id")
    private Long ownerId;
    
    @Schema(description = "所有者类型（ROLE-角色, USER-用户等）")
    private DataResourceType ownerType;
    
    @Schema(description = "数据类型（ORG-机构, COMPANY-公司等）")
    private DataResourceType dataType;
    
    @Schema(description = "权限范围类型: 10-个人, 20-自定义, 30-本级, 40-本级及子级, 50-全部")
    private Integer scopeType;
    
    @Schema(description = "数据ID（自定义时的具体数据，非自定义时为null）")
    @TableField("data_id")
    private Long dataId;
}
