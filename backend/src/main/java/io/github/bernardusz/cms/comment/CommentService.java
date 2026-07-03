package io.github.bernardusz.cms.comment;

import io.github.bernardusz.cms.comment.dto.CommentCreation;
import io.github.bernardusz.cms.comment.dto.CommentDetail;
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
  public List<CommentDetail> findPagination(Long contentId, Long userId, int limit, int offsets) {
    return commentRepository.findPagination(contentId, userId, limit, offsets);
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
  public void increaseLike(Long commentId, Long userId) {
    commentRepository.increaseLike(commentId, userId);
  }

  @Transactional
  public void decreaseLike(Long commentId, Long userId) {
    commentRepository.decreaseLike(commentId, userId);
  }

  @Transactional
  public void increaseDislike(Long commentId, Long userId) {
    commentRepository.increaseDislike(commentId, userId);
  }

  @Transactional
  public void decreaseDislike(Long commentId, Long userId) {
    commentRepository.decreaseDislike(commentId, userId);
  }
}
