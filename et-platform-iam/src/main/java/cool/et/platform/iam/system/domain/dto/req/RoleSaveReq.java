package cool.et.platform.iam.system.domain.dto.req;

import cool.et.framework.commons.security.DataScopeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 保存角色对象
 */
@Data
@Schema(name = "RoleSaveReq")
public class RoleSaveReq {

    @Schema(description = "角色名称")
    @NotEmpty(message = "角色名称不能为空")
    @Length(max = 30, message = "角色名称长度不能超过30")
    private String name;

    @Schema(description = "角色编码")
    @Length(max = 20, message = "角色编码长度不能超过20")
    private String code;

    @Schema(description = "描述")
    @Length(max = 100, message = "描述长度不能超过100")
    private String description;

    @NotNull(message = "角色状态不能为空")
    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "角色数据范围")
    private DataScopeType scopeType;

    @Schema(description = "自定义数据范围的组织 ID")
    private List<Long> orgIdList;
}
