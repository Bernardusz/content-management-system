package io.github.bernardusz.cms.comment.dto;

import java.time.LocalDateTime;

public record CommentDetail(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt,
    LocalDateTime edited,
    Long likesCount,
    Long dislikesCount,
    Long userId,
    Long contentId,
    boolean alreadyLiked,
    boolean alreadyDisliked
) { }
