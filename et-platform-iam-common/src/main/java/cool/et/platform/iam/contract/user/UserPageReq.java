package cool.et.platform.iam.contract.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(onConstructor_ = @JsonCreator)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserPageReq {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "性别编码，0 为未知，1 为男，2 为女", allowableValues = {"0", "1", "2"})
    private String sex;

    @Schema(description = "组织ID")
    private Long orgId;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "状态")
    private Boolean status;

    @Builder.Default
    @Schema(description = "当前页码", defaultValue = "1")
    private long current = 1;

    @Builder.Default
    @Schema(description = "每页条数", defaultValue = "10")
    private long size = 10;

    @Schema(description = "排序字段", example = "createTime")
    private String column;

    @Builder.Default
    @Schema(description = "是否升序", defaultValue = "true")
    private Boolean asc = true;
}
