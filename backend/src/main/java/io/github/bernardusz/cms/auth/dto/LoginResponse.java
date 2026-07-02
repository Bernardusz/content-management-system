package io.github.bernardusz.cms.auth.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    long expiresIn,
    long refreshTokenExpiresIn
) { }
