package cool.et.framework.boot.log.event;

import cool.et.framework.boot.log.AccessLogInfo;
import org.springframework.context.ApplicationEvent;

/**
 * 系统日志事件
 *
 * @since 2019-07-01 15:13
 */
public class AccessLogEvent extends ApplicationEvent {

    public AccessLogEvent(AccessLogInfo source) {
        super(source);
    }
}
