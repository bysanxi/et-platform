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
 * SexType
 *
 * @since 2020-02-14
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "枚举")
@JsonFormat
public enum Sex implements DictEnum<String> {
    
    /**
     * 按钮
     */
    UNKONW("0", "未知"),
    /**
     * 男
     */
    MAN("1", "男"),
    /**
     * 女
     */
    WOMAN("2", "女");
    
    @EnumValue
    @JsonValue
    private String value;
    
    @Schema(description = "描述")
    private String label;
    
    @JsonCreator
    public static Sex of(String type) {
        if (type == null) {
            return null;
        }
        for (Sex info : values()) {
            if (info.value.equals(type)) {
                return info;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
