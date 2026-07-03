package io.github.bernardusz.cms.comment;

import io.github.bernardusz.cms.comment.dto.CommentCreation;
import io.github.bernardusz.cms.comment.dto.CommentUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  @Transactional
  public Optional<Long> save(CommentCreation commentCreation) {
    return commentRepository.save(commentCreation);
  }

  @Transactional(readOnly = true)
  public List<Comment> findPagination(Long contentId, int limit, int offsets) {
    return commentRepository.findPagination(contentId, limit, offsets);
  }

  @Transactional
  public void updateById(Long id, CommentUpdate commentUpdate) {
    commentRepository.updateById(id, commentUpdate);
  }

  @Transactional
  public void deleteById(Long id) {
    commentRepository.deleteById(id);
  }

  @Transactional
  public void increaseLike(Long id) {
    commentRepository.increaseLike(id);
  }

  @Transactional
  public void decreaseLike(Long id) {
    commentRepository.decreaseLike(id);
  }

  @Transactional
  public void increaseDislike(Long id) {
    commentRepository.increaseDislike(id);
  }

  @Transactional
  public void decreaseDislike(Long id) {
    commentRepository.decreaseDislike(id);
  }
}
