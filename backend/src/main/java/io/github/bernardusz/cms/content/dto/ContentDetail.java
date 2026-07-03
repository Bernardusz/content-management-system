package io.github.bernardusz.cms.content.dto;

import java.time.LocalDateTime;

public record ContentDetail(
    Long id,
    String title,
    String description,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long commentsCount,
    Long likesCount,
    Long dislikesCount,
    Long userId
) { }
