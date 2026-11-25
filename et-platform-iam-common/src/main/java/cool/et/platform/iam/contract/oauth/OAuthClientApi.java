package cool.et.platform.iam.contract.oauth;

public interface OAuthClientApi {

    record Registration(String clientId, String clientSecret, Long userId, String tenantCode) {}
    record Binding(Long userId, String tenantCode) {}
    record NewSecret(String clientSecret) {}
    record Client(String clientId, Long userId, String tenantCode, boolean enabled, long authzVersion) {}

    Client register(Registration registration);

    Client find(String clientId);

    Client bind(String clientId, Binding binding);

    Client disable(String clientId);

    Client revoke(String clientId);

    Client rotate(String clientId, NewSecret secret);
}
