package cool.et.platform.iam.auth.service;

import lombok.Getter;

@Getter
public class OAuthFailure extends RuntimeException {
    private final int status;
    private final String challenge;

    public OAuthFailure(int status, String error, String challenge) {
        super(error);
        this.status = status;
        this.challenge = challenge;
    }

    public static OAuthFailure invalidClient() {
        return new OAuthFailure(401, "invalid_client", "Basic realm=\"iam\"");
    }

    public static OAuthFailure invalidToken() {
        return new OAuthFailure(401, "invalid_token", "Bearer realm=\"iam\", error=\"invalid_token\"");
    }

    public static OAuthFailure badRequest(String error) {
        return new OAuthFailure(400, error, null);
    }
}
