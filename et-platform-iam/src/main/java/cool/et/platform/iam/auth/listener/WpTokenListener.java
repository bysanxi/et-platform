package cool.et.platform.iam.auth.listener;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import cool.et.framework.commons.JacksonUtils;
import cool.et.framework.commons.NativeUserAgent;
import cool.et.framework.commons.RegionUtils;
import cool.et.framework.db.utils.TenantHelper;
import cool.et.framework.security.configuration.SecurityExtProperties;
import cool.et.framework.security.domain.UserInfoDetails;
import cool.et.platform.iam.auth.domain.entity.LoginLog;
import cool.et.platform.iam.auth.repository.LoginLogMapper;
import cool.et.platform.iam.auth.service.PasswordLoginService;
import cool.et.platform.iam.auth.support.domain.UserTenantAuthentication;
import cool.et.platform.iam.system.domain.entity.User;
import cool.et.platform.iam.system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 登录监听器
 */
@Component
@RequiredArgsConstructor
public class WpTokenListener implements SaTokenListener {

    private static final String USER_AGENT = "User-Agent";
    private final SecurityExtProperties extProperties;
    private final LoginLogMapper loginLogMapper;
    private final UserService userService;
    private final HttpServletRequest request;

    /**
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue 本次登录产生的 token 值
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
        String ip = JakartaServletUtil.getClientIP(request);
        String region = RegionUtils.getRegion(ip);
        String ua = request.getHeader(USER_AGENT);
        var userAgent = NativeUserAgent.parse(ua);
        final long userId = (long) loginId;
        UserTenantAuthentication authentication = SaHolder.getStorage().getModel(PasswordLoginService.AUTHENTICATION, UserTenantAuthentication.class);
        UserInfoDetails info = this.userService.userinfo(authentication);
        LoginLog loginLog = LoginLog.builder().principal(info.getUsername()).clientId(loginParameter.getDeviceType())
                .tenantId(info.getTenantId()).tenantCode(info.getTenantCode()).location(region).ip(ip)
                .platform(userAgent.platform()).engine(userAgent.engine()).browser(userAgent.browser()).os(userAgent.os())
                .loginType(PasswordLoginService.LOGIN_TYPE).createBy(userId).createTime(Instant.now()).createName(info.getNickname()).build();
        info.setLoginLog(JacksonUtils.readValue(JacksonUtils.toJson(loginLog), new TypeReference<>() {
        }));
        StpUtil.getTokenSessionByToken(tokenValue).set(extProperties.getServer().getTokenInfoKey(), info);
        // 记录登录日志
        TenantHelper.executeWithTenantDb(info.getTenantCode(), () -> this.loginLogMapper.insert(loginLog));
        // 刷新登录时间和IP
        this.userService.updateById(User.builder().id(userId).lastLoginIp(ip).lastLoginTime(Instant.now()).build());
    }

    @Override
    public void doRenewTimeout(String loginType, Object loginId, String tokenValue, long timeout) {

    }

    /**
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token值
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
    }

    /**
     * 每次被踢下线时触发
     *
     * @param loginType  账号类别
     * @param loginId    账号id
     * @param tokenValue token值
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        // 数据库可以记录一下操作日志
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {

    }

    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {

    }

    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {

    }

    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {

    }

    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {

    }

    @Override
    public void doCreateSession(String id) {

    }

    @Override
    public void doLogoutSession(String id) {

    }


}