package cool.et.platform.iam.base.domain.dto.req;

import cool.et.framework.db.mybatisplus.page.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "OptLogPageReq")
public class OptLogPageReq extends PageRequest {
    
    @Schema(description = "HTTP 请求方式")
    private String httpMethod;
    
    @Schema(description = "请求平台")
    private String platform;
    
    @Schema(description = "日志状态")
    private Boolean status;
    
}
