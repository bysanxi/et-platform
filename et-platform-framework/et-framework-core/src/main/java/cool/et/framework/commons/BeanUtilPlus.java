package cool.et.framework.commons;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Bean工具类增强版
 * <p>继承自Hutool的{@link BeanUtil}，提供额外的便捷转换方法</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 单个对象转换并设置ID
 * UserVO vo = BeanUtilPlus.toBean(1L, userEntity, UserVO.class);
 *
 * // 批量转换列表
 * List<UserVO> voList = BeanUtilPlus.toBeans(entities, UserVO.class);
 * }</pre>
 *
 * @since 1.0.0
 * @see BeanUtil
 */
public final class BeanUtilPlus extends BeanUtil {

    /**
     * 私有构造函数，防止实例化
     */
    private BeanUtilPlus() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 对象转Bean并设置ID
     *
     * @param id     要设置的ID值
     * @param source 源对象（Bean或Map）
     * @param clazz  目标Bean类型
     * @param <T>    目标类型
     * @return 转换后的Bean对象
     */
    public static <T> T toBean(Object id, Object source, Class<T> clazz) {
        T bean = toBean(source, clazz);
        ReflectUtil.setFieldValue(bean, "id", id);
        return bean;
    }

    /**
     * 批量转换集合为目标类型列表
     * <p>使用并行流处理，适合大数据量场景</p>
     * <p><b>注意：</b>如果Bean中包含枚举类型，请勿使用此方法</p>
     *
     * @param sourceList       源数据集合
     * @param destinationClass 目标类型
     * @param <T>              目标类型
     * @param <E>              源类型
     * @return 转换后的列表，如果源为空则返回空列表
     */
    public static <T, E> List<T> toBeans(Collection<E> sourceList, Class<T> destinationClass) {
        if (sourceList == null || sourceList.isEmpty() || destinationClass == null) {
            return Collections.emptyList();
        }
        return sourceList.parallelStream()
                .filter(Objects::nonNull)
                .map(source -> toBean(source, destinationClass))
                .toList();
    }
}
