package cool.et.platform.iam.auth.controller;

import cn.dev33.satoken.same.SaSameUtil;
import cool.et.framework.commons.entity.Result;
import cool.et.platform.iam.auth.service.OAuthClientRegistry;
import cool.et.platform.iam.contract.oauth.OAuthClientApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/oauth2/clients")
@RequiredArgsConstructor
public class OAuthClientManagementController implements OAuthClientApi {
    private final OAuthClientRegistry registry;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<?>> invalidBody() {
        return ResponseEntity.badRequest().body(Result.fail(400, "无效的客户端管理请求"));
    }

    @Override
    @PostMapping
    public Client register(@RequestBody Registration registration) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.register(registration);
    }

    @Override
    @GetMapping("/{clientId}")
    public Client find(@PathVariable("clientId") String clientId) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.find(clientId);
    }

    @Override
    @PutMapping("/{clientId}/binding")
    public Client bind(@PathVariable("clientId") String clientId, @RequestBody Binding binding) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.bind(clientId, binding);
    }

    @Override
    @PostMapping("/{clientId}/disable")
    public Client disable(@PathVariable("clientId") String clientId) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.disable(clientId);
    }

    @Override
    @PostMapping("/{clientId}/revoke")
    public Client revoke(@PathVariable("clientId") String clientId) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.revoke(clientId);
    }

    @Override
    @PutMapping("/{clientId}/secret")
    public Client rotate(@PathVariable("clientId") String clientId, @RequestBody NewSecret secret) {
        SaSameUtil.checkCurrentRequestToken();
        return registry.rotate(clientId, secret.clientSecret());
    }
}
