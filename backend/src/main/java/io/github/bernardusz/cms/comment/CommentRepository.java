package io.github.bernardusz.cms.comment;

import io.github.bernardusz.cms.comment.dto.CommentCreation;
import io.github.bernardusz.cms.comment.dto.CommentUpdate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {
  private final JdbcClient jdbcClient;
  public CommentRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public Optional<Long> save(CommentCreation commentCreation) {
    return jdbcClient.sql(
        """
        INSERT INTO comments
        (title, content, user_id, content_id)
        VALUES
        (:title, :content, :userId, :contentId)
        RETURNING id
        """
    ).param("title", commentCreation.title())
        .param("content", commentCreation.content())
        .param("userId", commentCreation.userId())
        .param("contentId", commentCreation.contentId())
        .query(Long.class)
        .optional();
  }

  public List<Comment> findPagination(Long contentId, int limit, int offsets){
    return jdbcClient.sql(
        """
        SELECT * FROM comments
        WHERE content_id = :contentId
        ORDER BY
          likes_count DESC,
          dislikes_count ASC,
          updated_at DESC
        LIMIT :limit OFFSET :offsets;
        """
    ).param("contentId", contentId)
        .param("limit", limit)
        .param("offsets", offsets)
        .query(Comment.class)
        .list();
  }

  public void updateById(Long id, CommentUpdate commentUpdate) {
    jdbcClient.sql(
        """
        UPDATE comments SET
        title = :title, content = :content, edited = NOW()
        WHERE id = :id
        """
    ).param("title", commentUpdate.title())
        .param("content", commentUpdate.content())
        .param("id", id)
        .update();
  }

  public void deleteById(Long id){
    jdbcClient.sql(
        """
        DELETE FROM comments
        WHERE id = :id
        """
    ).param("id", id).update();
  }

  public void increaseLike(Long id){
    jdbcClient.sql(
            """
            UPDATE comments SET
            SET likes_count = COALESCE(likes_count, 0) + 1
            WHERE id = :id
            """
        ).param("id", id)
        .update();
  }

  public void decreaseLike(Long id){
    jdbcClient.sql(
            """
            UPDATE comments SET
            SET likes_count = COALESCE(likes_count, 0) - 1
            WHERE id = :id
            """
        ).param("id", id)
        .update();
  }

  public void increaseDislike(Long id){
    jdbcClient.sql(
            """
            UPDATE comments SET
            SET dislikes_count = COALESCE(dislikes_count, 0) + 1
            WHERE id = :id
            """
        ).param("id", id)
        .update();
  }

  public void decreaseDislike(Long id){
    jdbcClient.sql(
            """
            UPDATE comments SET
            SET dislikes_count = COALESCE(dislikes_count, 0) - 1
            WHERE id = :id
            """
        ).param("id", id)
        .update();
  }
}
