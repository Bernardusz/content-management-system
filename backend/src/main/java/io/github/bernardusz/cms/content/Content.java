package io.github.bernardusz.cms.content;

import java.time.LocalDateTime;

public record Content(
  Long id,
  String title,
  String description,
  String content,
  LocalDateTime createdAt,
  LocalDateTime updatedAt,
  Long commentsCount,
  Long likesCount,
  Long dislikesCount,
  boolean isPrivate,
  Long userId
) { }