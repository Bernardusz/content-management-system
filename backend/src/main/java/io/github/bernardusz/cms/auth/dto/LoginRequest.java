package io.github.bernardusz.cms.auth.dto;

public record LoginRequest(
    String username,
    String password
) { }
