package cool.et.platform.iam.system.domain.dto.req;

import cool.et.framework.commons.security.DataScopeType;
import cool.et.framework.db.mybatisplus.page.PageRequest;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RolePageReq")
public class RolePageReq extends PageRequest {
    
    @Schema(description = "名称")
    private String name;
    
    @Parameter(description = "是否禁用", in = ParameterIn.QUERY)
    @Schema(description = "是否禁用")
    private Boolean status;
    
    @Parameter(description = "数据类型范围", in = ParameterIn.QUERY)
    @Schema(description = "数据类型范围")
    private DataScopeType scopeType;
    
}
