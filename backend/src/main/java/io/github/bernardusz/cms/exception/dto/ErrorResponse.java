package io.github.bernardusz.cms.exception.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
  String message,
  int code,
  LocalDateTime timeStamp
) {}
