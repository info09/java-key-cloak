package app.profile_service.service;

import app.profile_service.dto.identity.Credential;
import app.profile_service.dto.identity.TokenExchangeParam;
import app.profile_service.dto.identity.UserCreationParam;
import app.profile_service.dto.request.RegistrationRequest;
import app.profile_service.dto.response.ProfileResponse;
import app.profile_service.entity.Profile;
import app.profile_service.exception.ErrorNormalizer;
import app.profile_service.repository.IdentityClient;
import app.profile_service.repository.ProfileRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final IdentityClient identityClient;
    private final ErrorNormalizer errorNormalizer;

    @Value("${idp.client_id}")
    @NonFinal
    private String clientId;

    @Value("${idp.client_secret}")
    @NonFinal
    private String clientSecret;

    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAll()
                .stream()
                .map(profile -> new ProfileResponse(profile.getProfileId(),
                        profile.getUserId(),
                        profile.getEmail(),
                        profile.getUserName(),
                        profile.getFirstName(),
                        profile.getLastName(),
                        profile.getDob()))
                .toList();
    }

    public ProfileResponse register(RegistrationRequest request) {
        try {
            //Tạo account in KeyCloak
            //Exchange Client token
            var token = identityClient.exchangeTokenClient(TokenExchangeParam.builder()
                    .client_id(clientId)
                    .client_secret(clientSecret)
                    .grant_type("client_credentials")
                    .scope("openid")
                    .build());

            log.info("Token {}: ", token);

            //Create user with client token
            var response = identityClient.createUser("Bearer " + token.getAccessToken(), UserCreationParam.builder()
                    .username(request.getUserName())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .enabled(true)
                    .emailVerified(false)
                    .credentials(List.of(Credential.builder().type("password").value(request.getPassword()).temporary(false).build()))
                    .build());

            //Get UserId
            var profile = Profile.builder().userName(request.getUserName())
                    .email(request.getEmail())
                    .userId(extractUserId(response))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .dob(request.getDob()).build();

            profile = profileRepository.save(profile);

            return ProfileResponse.builder()
                    .profileId(profile.getProfileId())
                    .userId(profile.getUserId())
                    .email(profile.getEmail())
                    .username(profile.getUserName())
                    .firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .dob(profile.getDob())
                    .build();
        } catch (FeignException e) {
            throw errorNormalizer.handleKeyCloakException(e);
        }

    }

    private String extractUserId(ResponseEntity<?> response) {
        var headers = response.getHeaders();
        var locationHeader = headers.get("Location");
        if (locationHeader != null) {
            var location = locationHeader.getFirst();
            if (location != null) {
                var splitStr = location.split("/");
                if (splitStr.length > 0) {
                    return splitStr[splitStr.length - 1];
                }
            }
        }
        return null;
    }
}
