package cool.et.framework.id.contract;

public interface ISnowWorker {
    long nextId() throws IdGeneratorException;
}
