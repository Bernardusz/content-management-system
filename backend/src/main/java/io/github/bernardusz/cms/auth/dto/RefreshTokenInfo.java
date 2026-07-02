package io.github.bernardusz.cms.auth.dto;

import java.time.Instant;

public record RefreshTokenInfo(
    Long id,
    Long userId,
    String tokenHash,
    String salt,
    Instant expiresAt
) {}
