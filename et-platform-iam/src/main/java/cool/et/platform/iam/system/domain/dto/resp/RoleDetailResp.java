package cool.et.platform.iam.system.domain.dto.resp;

import cool.et.framework.commons.security.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * <p>
 * 实体类
 * 角色
 * </p>
 *
 * @since 2019-11-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class RoleDetailResp implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "租户ID")
    private Long tenantId;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "超期管理员")
    private Boolean superRole;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "内置角色")
    private Boolean readonly;

    @Schema(description = "角色状态")
    private Boolean status;

    @Schema(description = "权限范围")
    private DataScopeType scopeType;

    @Schema(description = "创建时间")
    private Instant createTime;

    @Schema(description = "自定义")
    private List<Long> orgIdList;


}
