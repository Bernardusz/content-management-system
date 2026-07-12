package io.github.bernardusz.cms.auth;

import io.github.bernardusz.cms.auth.dto.LoginRequest;
import io.github.bernardusz.cms.auth.dto.LoginResponse;
import io.github.bernardusz.cms.auth.dto.RefreshTokenInfo;
import io.github.bernardusz.cms.auth.service.JwtService;
import io.github.bernardusz.cms.exception.exceptions.RefreshTokenException;
import io.github.bernardusz.cms.exception.exceptions.UserAlreadyExistsException;
import io.github.bernardusz.cms.user.User;
import io.github.bernardusz.cms.user.UserRepository;
import io.github.bernardusz.cms.user.UserSecurity;
import io.github.bernardusz.cms.user.dto.UserCreation;
import io.github.bernardusz.cms.user.dto.UserDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final AuthRepository authRepository;

	public AuthService(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager,
		JwtService jwtService,
		AuthRepository authRepository
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.authRepository = authRepository;
	}

	@Transactional
	public void registerUser(UserCreation userCreation) {
		if (userRepository.existsByUsername(userCreation.username())) {
			throw new UserAlreadyExistsException("User already exists");
		}
		String hashedPassword = passwordEncoder.encode(userCreation.password());
		userRepository.save(
			new UserCreation(
				userCreation.username(),
				userCreation.email(),
				hashedPassword
			)
		);
	}

	@Transactional
	public LoginResponse loginUser(LoginRequest loginRequest) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				loginRequest.username(),
				loginRequest.password()
			)
		);

		User user = userRepository
			.findByIdentifierSecurity(loginRequest.username())
			.orElseThrow(() ->
				new UsernameNotFoundException("Username doesn't exist")
			);

		String accessToken = jwtService.generateToken(new UserSecurity(user));
		Map<String, String> refreshToken = jwtService.generateEncryptedRefreshToken(user.id());

		String salt = jwtService.generateSalt();
		String tokenHash = jwtService.hashToken(refreshToken.get("token"), salt);

		authRepository.saveRefreshToken(
			user.id(),
			tokenHash,
			refreshToken.get("sessionId"),
			salt,
			jwtService.getRefreshTokenExpiration()
		);

		return new LoginResponse(
			accessToken,
			refreshToken.get("token"),
			jwtService.getAccessTokenExpiration(),
			jwtService.getRefreshTokenExpiration()
		);
	}

	@Transactional
	public LoginResponse refreshAccessToken(String refreshToken) {
		String uuid = jwtService.decryptRefreshToken(refreshToken);

		if (!uuid.contains(":")) {
			throw new RefreshTokenException("Malformed refresh token structure.");
		}

		String[] parts = uuid.split(":");
    	Long userId = Long.parseLong(parts[0]);
		String sessionId = parts[1];

		RefreshTokenInfo tokenInfo = authRepository.findRefreshTokenBySessionIdAndUserId(sessionId ,userId)
			.orElseThrow(() -> new RefreshTokenException("No refresh token found"));

		String storedTokenHash = jwtService.hashToken(refreshToken, tokenInfo.salt());

		if (!storedTokenHash.equals(tokenInfo.tokenHash())) {
			authRepository.deleteRefreshTokenBySessionIdAndUserId(sessionId, userId);
			throw new RuntimeException("Invalid refresh token");
		}

		if (tokenInfo.expiresAt().isBefore(Instant.now())) {
			authRepository.deleteRefreshTokenBySessionIdAndUserId(sessionId, userId);
			throw new RuntimeException("Refresh token expired");
		}

		User user = userRepository.findByIdSecurity(userId)
        	.orElseThrow(() -> new RefreshTokenException("User no longer exists"));

		String newAccessToken = jwtService.generateToken(new UserSecurity(user));
		Map<String, String> newRefreshToken = jwtService.generateEncryptedRefreshToken(user.id());

		String newSalt = jwtService.generateSalt();
		String newTokenHash = jwtService.hashToken(newRefreshToken.get("token"), newSalt);

		authRepository.deleteRefreshTokenBySessionIdAndUserId(sessionId, userId);;
		authRepository.saveRefreshToken(
			user.id(),
			newTokenHash,
			newRefreshToken.get("sessionId"),
			newSalt,
			jwtService.getRefreshTokenExpiration()
		);

		return new LoginResponse(
			newAccessToken,
			newRefreshToken.get("token"),
			jwtService.getAccessTokenExpiration(),
			jwtService.getRefreshTokenExpiration()
		);
	}

	@Transactional
	public void revokeRefreshToken(String refreshToken) {
		String decryptedPayload = jwtService.decryptRefreshToken(refreshToken);
		String[] parts = decryptedPayload.split(":");
		Long userId = Long.parseLong(parts[0]);
		String sessionId = parts[1];

		// Wipe out ONLY the session row belonging to this specific device log-out
		authRepository.deleteRefreshTokenBySessionIdAndUserId(sessionId, userId);
	}
}
