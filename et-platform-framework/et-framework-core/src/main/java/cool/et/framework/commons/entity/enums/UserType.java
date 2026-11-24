package cool.et.framework.commons.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import cool.et.framework.commons.entity.DictEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * UserType
 *
 * @since 2020-02-14
 */
@Getter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户类型")
public enum UserType implements DictEnum<String> {

    /**
     * 内部用户、外部用户
     */
    PLATFORM_ADMIN("0", "平台管理员"),
    TENANT_ADMIN("1", "租户管理员"),
    IN("10", "普通用户（内部）"),
    OUT("20", "普通用户（外部）"),
    ;

    @EnumValue
    @JsonValue
    private String value;

    @Schema(description = "描述")
    private String label;

    @JsonCreator
    public static UserType of(String type) {
        if (type == null) {
            return null;
        }
        for (UserType info : values()) {
            if (info.value.equals(type)) {
                return info;
            }
        }
        return null;
    }

    public static boolean isAdmin(UserType userType) {
        return userType == PLATFORM_ADMIN || userType == TENANT_ADMIN;
    }

}
