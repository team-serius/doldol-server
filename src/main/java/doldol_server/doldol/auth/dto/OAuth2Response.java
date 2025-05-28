package doldol_server.doldol.auth.dto;

import doldol_server.doldol.user.entity.SocialType;

public interface OAuth2Response {

	String getSocialId();

	String getEmail();

	SocialType getSocialType();
}