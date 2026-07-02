package io.github.bernardusz.cms.user.dto;

import java.time.LocalDateTime;

public record UserSummary(
  Long id,
  String username,
  String email
) {}
