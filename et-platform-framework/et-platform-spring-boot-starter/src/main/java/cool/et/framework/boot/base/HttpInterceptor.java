package cool.et.framework.boot.base;

import cool.et.framework.commons.threadlocal.ThreadLocalHolder;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

/**
 * HTTP请求拦截器
 * <p>用于设置请求级别的线程上下文数据（如Locale等）</p>
 * <p>请求开始时设置，请求结束时清理，避免内存泄漏</p>
 *
 * @since 1.0.0
 */
@Slf4j
public class HttpInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object handler) {
        ThreadLocalHolder.clear();
        // 从请求头中获取地区信息
        Locale locale = request.getLocale();
        ThreadLocalHolder.setLocale(locale);
        log.debug("http header locale - {}", locale);
        return true;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Object object, Exception exception) {
        ThreadLocalHolder.clear();
    }
}
