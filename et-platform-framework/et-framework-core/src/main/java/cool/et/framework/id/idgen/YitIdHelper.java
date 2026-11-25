package cool.et.framework.id.idgen;

import cool.et.framework.id.contract.IIdGenerator;
import cool.et.framework.id.contract.IdGeneratorException;
import cool.et.framework.id.contract.IdGeneratorOptions;

/**
 * 这是一个调用的例子，默认情况下，单机集成者可以直接使用 nextId()。
 */
public class YitIdHelper {

    private static volatile IIdGenerator idGenInstance;

    public static IIdGenerator getIdGenInstance() {
        return idGenInstance;
    }

    /**
     * 设置参数，建议程序初始化时执行一次
     */
    public static void setIdGenerator(IdGeneratorOptions options) throws IdGeneratorException {
        idGenInstance = new DefaultIdGenerator(options);
    }

    /**
     * 生成新的Id
     * 调用本方法前，请确保调用了 SetIdGenerator 方法做初始化。
     *
     * @return
     */
    public static long nextId() throws IdGeneratorException {
        // if (idGenInstance == null) {
        // idGenInstance = new DefaultIdGenerator(new IdGeneratorOptions((short) 1));
        // }

        if (idGenInstance == null)
            throw new IdGeneratorException("Please initialize Yitter.IdGeneratorOptions first.");

        return idGenInstance.newLong();
    }
}
