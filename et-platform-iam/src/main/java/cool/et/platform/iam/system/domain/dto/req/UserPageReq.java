package cool.et.platform.iam.system.domain.dto.req;

import cool.et.framework.db.mybatisplus.page.PageRequest;
import cool.et.platform.iam.system.domain.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageReq extends PageRequest {
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "性别")
    private Sex sex;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "组织ID")
    private Long orgId;
    
    @Schema(description = "手机号")
    private String mobile;
    
    @Schema(description = "状态")
    private Boolean status;
    
}
