package cool.et.platform.iam.base.domain.dto.req;

import cool.et.framework.db.mybatisplus.page.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "LoginLogPageReq")
public class LoginLogPageReq extends PageRequest {
    
    @Schema(description = "账号")
    private String principal;
    
    @Schema(description = "名称")
    private String nickname;
    
    @Schema(description = "请求平台")
    private String platform;
    
}
