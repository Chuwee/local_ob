package es.onebox.event.datasources.ms.entity.dto;

import java.util.List;

public record Login(
		ConfigAllowed allowedConfig,
		ConfigDefault defaultConfig) {

	public record ConfigAllowed(
			List<LoginMethod> loginMethods,
			Boolean invitations,
			Boolean editUserInfo,
			List<LoginRequest> loginRequest) {
	}

	public record ConfigDefault(
			LoginMethod loginMethod,
			boolean invitations,
			boolean editUserInfo,
			LoginRequest loginRequest) {
	}
}