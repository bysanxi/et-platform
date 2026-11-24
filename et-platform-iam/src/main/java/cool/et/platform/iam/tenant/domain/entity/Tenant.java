package cool.et.platform.iam.tenant.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.commons.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;


/**
 * <p>
 * 租户信息
 * </p>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_tenant")
public class Tenant extends SuperEntity<Long> {
    
    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "状态;0=未启用;1=启用")
    private Boolean status;
    @Schema(description = "别名")
    private String alias;
    
    @Schema(description = "LOGO")
    private String logo;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "联系人")
    private String contactPerson;
    @Schema(description = "联系方式")
    private String contactPhone;
    @Schema(description = "描述")
    private String description;
    
}
