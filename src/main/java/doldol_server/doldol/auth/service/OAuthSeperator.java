package doldol_server.doldol.auth.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.auth.dto.OAuth2ResponseStrategy;
import doldol_server.doldol.user.entity.SocialType;

@Component
public class OAuthSeperator {

    private final Map<String, OAuth2ResponseStrategy> strategies;

    public OAuthSeperator(List<OAuth2ResponseStrategy> strategyList) {
        this.strategies = strategyList.stream()
            .collect(Collectors.toMap(
                OAuth2ResponseStrategy::getProviderType,
                strategy -> strategy
            ));
    }

    public OAuth2Response createResponse(String registrationId, Map<String, Object> attributes) {

        SocialType socialType = SocialType.getSocialType(registrationId);
        OAuth2ResponseStrategy strategy = strategies.get(socialType.name());

        return strategy.createResponse(attributes);
    }
}