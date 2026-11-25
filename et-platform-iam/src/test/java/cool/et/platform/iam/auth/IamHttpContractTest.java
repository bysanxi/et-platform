package cool.et.platform.iam.auth;

import cool.et.platform.iam.auth.controller.OAuthClientManagementController;
import cool.et.platform.iam.auth.controller.UserAccountController;
import cool.et.platform.iam.feign.OAuthClientFeign;
import cool.et.platform.iam.feign.UserAccountFeign;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IamHttpContractTest {

    @Test
    void accountRoutesMatchOnClientAndServer() {
        assertRoutes(UserAccountFeign.class, new UserAccountController(null), 4);
    }

    @Test
    void oauthManagementRoutesMatchOnClientAndServer() {
        assertRoutes(OAuthClientFeign.class, new OAuthClientManagementController(null), 6);
    }

    @Test
    void accountEndpointValidatesTheBusinessRequest() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new UserAccountController(null)).build();
        var result = mvc.perform(post("/users/lookup").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenantCode":"","type":"username","value":"test"}
                                """))
                .andExpect(status().isBadRequest()).andReturn();
        assertThat(result.getResolvedException()).isInstanceOfSatisfying(MethodArgumentNotValidException.class,
                error -> assertThat(error.getBindingResult().getFieldError("tenantCode")).isNotNull());
    }

    private void assertRoutes(Class<?> client, Object controller, int expectedCount) {
        var context = MockMvcBuilders.standaloneSetup(controller).build()
                .getDispatcherServlet().getWebApplicationContext();
        var handlers = context.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
        Set<String> serverRoutes = new HashSet<>();
        handlers.forEach((mapping, handler) -> {
            for (var method : mapping.getMethodsCondition().getMethods()) {
                for (String path : mapping.getPatternValues()) {
                    serverRoutes.add(handler.getMethod().getName() + " " + method.name() + " " + path);
                }
            }
        });

        var clientRoutes = new SpringMvcContract().parseAndValidateMetadata(client).stream()
                .map(metadata -> metadata.method().getName() + " " + metadata.template().method()
                        + " " + metadata.template().url())
                .toList();
        assertThat(clientRoutes).hasSize(expectedCount).containsExactlyInAnyOrderElementsOf(serverRoutes);
    }
}
