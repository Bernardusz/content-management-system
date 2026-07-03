package io.github.bernardusz.cms.content.dto;

public record ContentUpdate(
    String title,
    String description,
    String content,
    boolean isPrivate
) { }
