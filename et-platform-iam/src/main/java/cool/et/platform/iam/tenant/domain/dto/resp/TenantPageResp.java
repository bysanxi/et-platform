package cool.et.platform.iam.tenant.domain.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;

@Data
public class TenantPageResp {
    
    private Long id;
    @Schema(description = "编码")
    private String code;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "状态")
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
    
    @Schema(description = "创建人")
    private String createName;
    @Schema(description = "创建时间")
    private Instant createTime;
    
}
