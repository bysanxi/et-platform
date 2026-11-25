package cool.et.platform.iam.auth.controller;

import cool.et.framework.commons.annotation.IgnoreGlobalResponse;
import cool.et.platform.iam.auth.service.OAuthClientTokens;
import cool.et.platform.iam.auth.service.OAuthFailure;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import static cool.et.platform.iam.auth.service.OAuthClientRegistry.GRANT;

@RestController
@RequestMapping("/oauth2")
@ConditionalOnProperty(prefix = "extend.security", name = "enabled-oauth2", havingValue = "true")
@RequiredArgsConstructor
public class OAuthClientTokenController {
    private final OAuthClientTokens tokens;

    @PostMapping("/token")
    @IgnoreGlobalResponse(description = "OAuth 2.0")
    public ResponseEntity<OAuthClientTokens.Token> token(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (request.getQueryString() != null || contentType == null
                || !MediaType.APPLICATION_FORM_URLENCODED_VALUE.equalsIgnoreCase(contentType.split(";", 2)[0].trim())
                || request.getParameterMap().values().stream().anyMatch(values -> values.length != 1)
                || request.getParameter("client_id") != null || request.getParameter("client_secret") != null) {
            throw OAuthFailure.badRequest("invalid_request");
        }
        String grant = request.getParameter("grant_type");
        if (grant == null || grant.isEmpty()) {
            throw OAuthFailure.badRequest("invalid_request");
        }
        if (!GRANT.equals(grant)) {
            throw OAuthFailure.badRequest("unsupported_grant_type");
        }
        String basic = credential(request, "Basic");
        String id;
        String secret;
        try {
            String pair = new String(Base64.getDecoder().decode(basic), StandardCharsets.UTF_8);
            int colon = pair.indexOf(':');
            if (colon < 1) {
                throw OAuthFailure.invalidClient();
            }
            id = URLDecoder.decode(pair.substring(0, colon), StandardCharsets.UTF_8);
            secret = URLDecoder.decode(pair.substring(colon + 1), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw OAuthFailure.invalidClient();
        }
        return response(200).body(tokens.issue(id, secret, request.getParameter("scope")));
    }

    @GetMapping("/identity")
    @IgnoreGlobalResponse(description = "OAuth 2.0")
    public ResponseEntity<OAuthClientTokens.Identity> identity(HttpServletRequest request) {
        return response(200).body(tokens.identity(credential(request, "Bearer")));
    }

    private static String credential(HttpServletRequest request, String scheme) {
        var headers = Collections.list(request.getHeaders(HttpHeaders.AUTHORIZATION));
        String prefix = scheme + " ";
        if (headers.size() == 1 && headers.getFirst().regionMatches(true, 0, prefix, 0, prefix.length())) {
            String value = headers.getFirst().substring(prefix.length()).stripLeading();
            if (!value.isBlank()) {
                return value;
            }
        }
        throw "Basic".equals(scheme) ? OAuthFailure.invalidClient() : OAuthFailure.invalidToken();
    }

    @ExceptionHandler(OAuthFailure.class)
    @IgnoreGlobalResponse(description = "OAuth 2.0")
    public ResponseEntity<Map<String, String>> rejected(OAuthFailure failure) {
        var response = response(failure.getStatus());
        if (failure.getChallenge() != null) {
            response.header(HttpHeaders.WWW_AUTHENTICATE, failure.getChallenge());
        }
        return response.body(Map.of("error", failure.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @IgnoreGlobalResponse(description = "OAuth 2.0")
    public ResponseEntity<Map<String, String>> unavailable() {
        return response(500).body(Map.of("error", "server_error"));
    }

    private static ResponseEntity.BodyBuilder response(int status) {
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noStore()).header(HttpHeaders.PRAGMA, "no-cache");
    }
}
