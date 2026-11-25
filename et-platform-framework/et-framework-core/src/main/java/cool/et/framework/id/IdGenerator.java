package cool.et.framework.id;

import cool.et.framework.id.contract.IdGeneratorOptions;
import cool.et.framework.id.idgen.DefaultIdGenerator;

/** 业务手工发号与 MyBatis-Plus 主键分配共用的生成器。 */
public class IdGenerator extends DefaultIdGenerator {

    public IdGenerator(IdGeneratorOptions options) {
        super(options);
    }

    public long next() {
        return newLong();
    }
}
