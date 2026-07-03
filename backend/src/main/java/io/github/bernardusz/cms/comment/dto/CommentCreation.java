package io.github.bernardusz.cms.comment.dto;

public record CommentCreation(
    String title,
    String content,
    Long userId,
    Long contentId
) { }
