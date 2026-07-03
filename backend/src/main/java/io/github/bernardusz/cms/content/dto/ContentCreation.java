package io.github.bernardusz.cms.content.dto;

import java.time.LocalDateTime;

public record ContentCreation(
    String title,
    String description,
    String content,
    boolean isPrivate,
    Long userId
) { }
