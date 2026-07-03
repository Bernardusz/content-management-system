package io.github.bernardusz.cms.comment;

import java.time.LocalDateTime;

public record Comment(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime edited,
    Long likesCount,
    Long dislikesCount,
    Long userId,
    Long contentId
) { }