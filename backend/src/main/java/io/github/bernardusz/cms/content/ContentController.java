package io.github.bernardusz.cms.content;

import io.github.bernardusz.cms.content.dto.ContentCreation;
import io.github.bernardusz.cms.content.dto.ContentDetail;
import io.github.bernardusz.cms.content.dto.ContentSummary;
import io.github.bernardusz.cms.content.dto.ContentUpdate;
import io.github.bernardusz.cms.exception.exceptions.FailedCreatingContent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contents")
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
      @RequestParam(required = false) String identifier,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset
  ) {
    return ResponseEntity.ok(
        contentService.findAllWithFilter(identifier, limit, offset)
    );
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContentDetail> findById(@PathVariable Long id) {
    return ResponseEntity.ok(
        contentService.findById(id)
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
  public ResponseEntity<Void> increaseLike(@PathVariable Long id) {
    contentService.increaseLike(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/like")
  public ResponseEntity<Void> decreaseLike(@PathVariable Long id) {
    contentService.decreaseLike(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/dislike")
  public ResponseEntity<Void> increaseDislike(@PathVariable Long id) {
    contentService.increaseDislike(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/dislike")
  public ResponseEntity<Void> decreaseDislike(@PathVariable Long id) {
    contentService.decreaseDislike(id);
    return ResponseEntity.ok().build();
  }
}
