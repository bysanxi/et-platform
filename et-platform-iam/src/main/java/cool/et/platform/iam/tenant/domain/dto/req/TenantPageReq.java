package cool.et.platform.iam.tenant.domain.dto.req;

import cool.et.framework.db.mybatisplus.page.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TenantPageReq")
public class TenantPageReq extends PageRequest {
    
    @Schema(description = "名称")
    private String name;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "状态")
    private Integer status;
}
