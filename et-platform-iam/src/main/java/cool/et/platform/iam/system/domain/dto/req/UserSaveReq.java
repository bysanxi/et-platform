package cool.et.platform.iam.system.domain.dto.req;

import cn.hutool.core.lang.RegexPool;
import cool.et.platform.iam.system.domain.enums.Sex;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 实体类
 * 用户
 *
 * @since 2020-02-14
 */
@Data
@Schema(name = "UserSaveReq", description = "保存用户信息实体")
public class UserSaveReq {
    
    @Schema(description = "账号")
    @NotBlank(message = "账号不能为空")
    @Length(max = 30, message = "账号长度不能超过{max}")
    private String username;
    
    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    @Length(max = 64, message = "密码长度不能超过{max}")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    
    @Schema(description = "姓名")
    @NotBlank(message = "姓名不能为空")
    @Length(max = 50, message = "姓名长度不能超过50")
    private String nickname;
    
    @Schema(description = "组织ID")
    private Long orgId;
    
    @Schema(description = "邮箱")
    @Length(max = 255, message = "邮箱长度不能超过 {max}")
    private String email;
    
    @Schema(description = "手机")
    @NotBlank(message = "手机号不能为空")
    @Length(max = 11, message = "手机长度不能超过20")
    @Pattern(regexp = RegexPool.MOBILE, message = "手机号格式错误")
    private String mobile;
    
    @Schema(description = "性别")
    private Sex sex;
    
    @Schema(description = "状态")
    private Boolean status;
    
    @Schema(description = "头像")
    @Length(max = 255, message = "头像长度不能超过 {max}")
    private String avatar;
    
    @Schema(description = "工作描述")
    @Length(max = 255, message = "描述长度不能超过{max}")
    private String description;
    
}
