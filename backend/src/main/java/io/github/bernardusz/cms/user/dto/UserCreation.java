package io.github.bernardusz.cms.user.dto;

import java.time.LocalDateTime;

public record UserCreation(
  String username,
  String email,
  String password
) {}
