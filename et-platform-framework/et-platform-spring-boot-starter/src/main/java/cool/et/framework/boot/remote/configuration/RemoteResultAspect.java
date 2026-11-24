package cool.et.framework.boot.remote.configuration;

import cool.et.framework.boot.remote.RemoteService;
import cool.et.framework.commons.annotation.remote.RemoteResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * RemoteResult 注解的 AOP 工具
 */
@Aspect
@Slf4j
@AllArgsConstructor
public class RemoteResultAspect {

    private final RemoteService remoteService;

    @Pointcut("@annotation(cool.et.framework.commons.annotation.remote.RemoteResult)")
    public void methodPointcut() {
    }

    @Around("methodPointcut() && @annotation(rr)")
    public Object interceptor(ProceedingJoinPoint pjp, RemoteResult rr) throws Throwable {
        Object proceed = pjp.proceed();
        remoteService.action(proceed, rr.ignoreFields());
        return proceed;
    }
}
