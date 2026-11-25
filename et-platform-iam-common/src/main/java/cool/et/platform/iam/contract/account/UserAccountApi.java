package cool.et.platform.iam.contract.account;

import cn.hutool.core.lang.RegexPool;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public interface UserAccountApi {

    enum LookupType { username, email, mobile }

    record Lookup(@NotBlank String tenantCode, @NotNull LookupType type,
                  @NotBlank @Size(max = 255) String value) {}
    record Account(Long id, String username, Boolean status) {}
    record Registration(@NotBlank String tenantCode, @Email @Size(max = 255) String email,
                        @Size(max = 11) @Pattern(regexp = RegexPool.MOBILE) String mobile,
                        @Pattern(regexp = PasswordPolicy.PATTERN) String password) {}
    record Registered(Long userId) {}
    record PasswordReset(@NotBlank String tenantCode, @NotBlank @Pattern(regexp = PasswordPolicy.PATTERN) String newPassword) {}
    record MobileLogin(@NotBlank String tenantCode, @NotBlank @Size(max = 11) @Pattern(regexp = RegexPool.MOBILE) String mobile) {}

    Account lookup(@Valid Lookup request);

    Registered register(@Valid Registration request);

    Registered resetPassword(Long userId, @Valid PasswordReset request);

    LoginResp mobileLogin(@Valid MobileLogin request);
}
