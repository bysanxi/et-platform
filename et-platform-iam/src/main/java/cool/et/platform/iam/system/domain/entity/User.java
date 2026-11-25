package cool.et.platform.iam.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.commons.entity.SuperEntity;
import cool.et.framework.commons.entity.enums.UserType;
import cool.et.platform.iam.system.domain.enums.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;

@Data
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user")
@Schema(name = "User", description = "用户")
public class User extends SuperEntity<Long> {
    
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户类型")
    private UserType type;
    
    @Schema(description = "租户ID")
    private Long tenantId;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "手机号")
    private String mobile;

    public User setEmail(String email) {
        this.email = email == null || email.isBlank() ? null : email.strip().toLowerCase(Locale.ROOT);
        return this;
    }
    
    @Schema(description = "性别")
    private Sex sex;
    
    @Schema(description = "头像")
    private String avatar;
    
    @Schema(description = "是否只读")
    private Boolean readonly;
    
    @Schema(description = "状态(false=禁用;true=启用)")
    private Boolean status;
    
    @Schema(description = "生日")
    private LocalDate birthday;
    
    @Schema(description = "机构ID")
    private Long orgId;
    
    @Schema(description = "最后登录IP")
    private String lastLoginIp;
    
    @Schema(description = "最后登录时间")
    private Instant lastLoginTime;
    
}
