package app.profile_service.repository;

import app.profile_service.dto.identity.TokenExchangeParam;
import app.profile_service.dto.identity.TokenExchangeResponse;
import app.profile_service.dto.identity.UserCreationParam;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "/realms/huytq/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeTokenClient(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "/admin/realms/huytq/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(@RequestHeader("Authorization") String token, @RequestBody UserCreationParam param);
}
