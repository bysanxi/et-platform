package cool.et.platform.iam.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import cool.et.framework.commons.entity.SuperEntity;
import cool.et.platform.iam.system.domain.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * 菜单
 *
 * @since 2019-11-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resource")
@Schema(name = "Resource", description = "资源")
public class Resource extends SuperEntity<Long> {

    @Schema(description = "归属应用")
    private String clientId;

    @Schema(description = "权限编码")
    @TableField(value = "permission")
    private String permission;

    @Schema(description = "名称")
    private String title;

    @Schema(description = "父级菜单ID")
    private Long parentId;

    @Schema(description = "类型")
    private ResourceType type;

    @Schema(description = "排序")
    private Integer sequence;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "组件")
    private String component;

    @Schema(description = "页面缓存，开启后页面会缓存，不会重新加载，仅在标签页启用时有效")
    private Boolean keepAlive;

    @Schema(description = "公共资源（无需分配所有人可访问）")
    private Boolean shared;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "状态")
    private Boolean status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "路由元信息(JSON)")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> meta;

}
