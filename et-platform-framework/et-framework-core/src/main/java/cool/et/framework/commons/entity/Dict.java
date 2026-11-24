package cool.et.framework.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @param <T> T
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dict<T> {
    
    /**
     * 值
     */
    private T value;
    
    /**
     * 标题
     */
    private String label;
    
}
