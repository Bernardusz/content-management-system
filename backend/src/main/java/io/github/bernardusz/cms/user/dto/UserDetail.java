package io.github.bernardusz.cms.user.dto;

import java.time.LocalDateTime;

public record UserDetail(
  Long id,
  String username,
  String email,
  LocalDateTime createdAt
) {}
