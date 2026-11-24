package cool.et.framework.boot.log.handler;


import cool.et.framework.boot.log.AccessLogInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认的日志处理
 */
@Slf4j
public class DefaultHandlerAbstract extends AbstractLogHandler {

    /**
     * 执行日志打印
     */
    @Override
    public AccessLogInfo handler(AccessLogInfo info) {
        return info;
    }
}