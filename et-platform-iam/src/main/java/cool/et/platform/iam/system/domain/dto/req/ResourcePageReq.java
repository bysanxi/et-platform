package cool.et.platform.iam.system.domain.dto.req;

import cool.et.framework.db.mybatisplus.page.PageRequest;
import cool.et.platform.iam.system.domain.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ResourcePageReq")
public class ResourcePageReq extends PageRequest {
    
    @Schema(description = "父级ID")
    private Long parentId;
    
    @Schema(description = "类型")
    private ResourceType type;
    
}
