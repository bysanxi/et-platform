package cool.et.platform.iam.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 角色的资源
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_role_res")
@EqualsAndHashCode(callSuper = true)
public class RoleRes extends SuperEntity<Long> {

    @Schema(description = "资源ID")
    private Long resId;
    
    @Schema(description = "角色ID")
    private Long roleId;
    
}
