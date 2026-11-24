package cool.et.platform.iam.system.domain.enums;

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
 * 资源类型-枚举
 */
@Getter
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "资源类型-枚举")
public enum ResourceType implements DictEnum<String> {

    DIRECTORY("directory", "目录"),
    MENU("menu", "菜单"),
    IFRAME("iframe", "内嵌"),
    LINK("link", "外链"),
    BUTTON("button", "按钮"),
    ;

    @EnumValue
    @JsonValue
    @Schema(description = "资源类型")
    private String value;

    @Schema(description = "描述")
    private String label;

    @JsonCreator
    public static ResourceType of(String type) {
        if (type == null) {
            return null;
        }
        for (ResourceType info : values()) {
            if (info.value.equals(type)) {
                return info;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }

}
