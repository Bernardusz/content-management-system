package io.github.bernardusz.cms.content.dto;

import java.time.LocalDateTime;

public record ContentSummary(
    Long id,
    String title,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long commentsCount,
    Long likesCount,
    Long dislikesCount,
    Long userId
) { }
