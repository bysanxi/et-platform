package cool.et.framework.foundation.domain;

/**
 * <p><b>标记领域对象的修改状态</b></p>
 * 新增：表示新建的对象，数据库还没有，需要向数据表插入记录。<P>
 * 不变：表示从数据库里取出的对象，数据没有变化，因此不需要任何数据库操作。<P>
 * 更改：表示从数据库里取出的对象，数据发生了变化，需要在数据表里更改记录。<P>
 * 删除：表示从数据库里取出的对象，需要在数据表里删除记录。
 * <P>
 * @see cool.et.framework.foundation.domain.Entity
 * @author Yang, X.D.
 * @since 2023-07-06 12:52 PM
 */
public enum ChangingStatus {
    NEW, // 新增
    UNCHANGED, // 不变
    UPDATED, // 更改
    DELETED // 删除
}
