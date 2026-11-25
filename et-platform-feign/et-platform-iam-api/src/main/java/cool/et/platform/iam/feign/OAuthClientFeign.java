package cool.et.platform.iam.feign;

import cool.et.framework.commons.FeignConstants;
import cool.et.platform.iam.configure.IamServiceFeignConfiguration;
import cool.et.platform.iam.contract.oauth.OAuthClientApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = FeignConstants.AUTH_FEIGN_NAME, contextId = "oauthClientManagement",
        configuration = IamServiceFeignConfiguration.class)
public interface OAuthClientFeign extends OAuthClientApi {
    String PATH = "/internal/oauth2/clients";

    @Override
    @PostMapping(PATH)
    Client register(@RequestBody Registration registration);

    @Override
    @GetMapping(PATH + "/{clientId}")
    Client find(@PathVariable("clientId") String clientId);

    @Override
    @PutMapping(PATH + "/{clientId}/binding")
    Client bind(@PathVariable("clientId") String clientId, @RequestBody Binding binding);

    @Override
    @PostMapping(PATH + "/{clientId}/disable")
    Client disable(@PathVariable("clientId") String clientId);

    @Override
    @PostMapping(PATH + "/{clientId}/revoke")
    Client revoke(@PathVariable("clientId") String clientId);

    @Override
    @PutMapping(PATH + "/{clientId}/secret")
    Client rotate(@PathVariable("clientId") String clientId, @RequestBody NewSecret secret);
}
