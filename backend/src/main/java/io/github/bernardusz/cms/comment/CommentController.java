package io.github.bernardusz.cms.comment;

import io.github.bernardusz.cms.comment.dto.CommentCreation;
import io.github.bernardusz.cms.comment.dto.CommentUpdate;
import io.github.bernardusz.cms.exception.exceptions.FailedCreatingComment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody CommentCreation commentCreation) {
    return ResponseEntity.created(
        commentService.save(commentCreation).map(
            id -> {
              URI uri = ServletUriComponentsBuilder
                  .fromCurrentRequest()
                  .path("/{id}")
                  .buildAndExpand(id)
                  .toUri();
              return uri;
            }
        ).orElseThrow(
            () -> new FailedCreatingComment("Comment creation failed")
        )
    ).build();
  }

  @GetMapping
  public ResponseEntity<List<Comment>> findPagination(
      @RequestParam Long contentId,
      @RequestParam(defaultValue = "10") int limit,
      @RequestParam(defaultValue = "0") int offset
  ) {
    return ResponseEntity.ok(
        commentService.findPagination(contentId, limit, offset)
    );
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CommentUpdate commentUpdate) {
    commentService.updateById(id, commentUpdate);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    commentService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/like")
  public ResponseEntity<Void> increaseLike(@PathVariable Long id) {
    commentService.increaseLike(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/like")
  public ResponseEntity<Void> decreaseLike(@PathVariable Long id) {
    commentService.decreaseLike(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{id}/dislike")
  public ResponseEntity<Void> increaseDislike(@PathVariable Long id) {
    commentService.increaseDislike(id);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}/dislike")
  public ResponseEntity<Void> decreaseDislike(@PathVariable Long id) {
    commentService.decreaseDislike(id);
    return ResponseEntity.ok().build();
  }
}
