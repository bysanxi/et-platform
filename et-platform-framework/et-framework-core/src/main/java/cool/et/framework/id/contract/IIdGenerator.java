package cool.et.framework.id.contract;

public interface IIdGenerator {
    long newLong() throws IdGeneratorException;
}
