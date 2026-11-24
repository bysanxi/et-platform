package cool.et.platform.iam.base.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cool.et.framework.boot.log.AccessLogInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("c_opt_log")
public class OptLog extends AccessLogInfo {
    
    @TableId(type = IdType.ASSIGN_ID)
    @OrderBy
    private Long id;
    
}
