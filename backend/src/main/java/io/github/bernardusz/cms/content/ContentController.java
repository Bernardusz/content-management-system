package io.github.bernardusz.cms.content;

import io.github.bernardusz.cms.content.dto.ContentCreation;
import io.github.bernardusz.cms.content.dto.ContentDetail;
import io.github.bernardusz.cms.content.dto.ContentSummary;
import io.github.bernardusz.cms.content.dto.ContentUpdate;
import io.github.bernardusz.cms.exception.exceptions.FailedCreatingContent;
import io.github.bernardusz.cms.user.UserSecurity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contents")
public class ContentController {
  private final ContentService contentService;

  public ContentController(ContentService contentService) {
    this.contentService = contentService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody ContentCreation contentCreation) {
    return ResponseEntity.created(
        contentService.save(contentCreation).map(
            id -> {
              URI uri = ServletUriComponentsBuilder
                  .fromCurrentRequest()
                  .path("/{id}")
                  .buildAndExpand(id)
                  .toUri();
              return uri;
            }
        ).orElseThrow(
            () -> new FailedCreatingContent("Content creation failed")
        )
    ).build();
  }

  @GetMapping
  public ResponseEntity<List<ContentSummary>> findAll(
      @AuthenticationPrincipal UserSecurity user,
      @RequestParam(required = false) String identifier,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset
  ) {
    return ResponseEntity.ok(
        contentService.findAllWithFilter(user.getId(), identifier, limit, offset)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContentDetail> findById(@PathVariable Long id, @AuthenticationPrincipal UserSecurity user) {
    return ResponseEntity.ok(
        contentService.findById(id, user.getId())
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ContentUpdate contentUpdate) {
    contentService.updateById(id, contentUpdate);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    contentService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<Void> increaseLike(
      @PathVariable Long id,
      @AuthenticationPrincipal UserSecurity user
  ) {
    contentService.increaseLike(id, user.getId());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/like")
  public ResponseEntity<Void> decreaseLike(
      @PathVariable Long id,
      @AuthenticationPrincipal UserSecurity user
  ) {
    contentService.decreaseLike(id, user.getId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/dislike")
  public ResponseEntity<Void> increaseDislike(
      @PathVariable Long id,
      @AuthenticationPrincipal UserSecurity user
  ) {
    contentService.increaseDislike(id, user.getId());
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/dislike")
  public ResponseEntity<Void> decreaseDislike(
      @PathVariable Long id,
      @AuthenticationPrincipal UserSecurity user
  ) {
    contentService.decreaseDislike(id, user.getId());
    return ResponseEntity.ok().build();
  }
}
