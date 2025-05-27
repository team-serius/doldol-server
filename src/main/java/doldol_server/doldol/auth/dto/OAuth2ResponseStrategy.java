package doldol_server.doldol.auth.dto;

import java.util.Map;

public interface OAuth2ResponseStrategy {
	String getProviderType();

	OAuth2Response createResponse(Map<String, Object> attributes);
}