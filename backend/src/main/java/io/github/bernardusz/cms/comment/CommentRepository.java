package io.github.bernardusz.cms.comment;

import io.github.bernardusz.cms.comment.dto.CommentCreation;
import io.github.bernardusz.cms.comment.dto.CommentDetail;
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

  public List<CommentDetail> findPagination(Long contentId, Long userId, int limit, int offsets){
    return jdbcClient.sql(
        """
        SELECT
          c.id,
          c.title,
          c.content,
          c.created_at,
          c.edited,
          c.likes_count,
          c.dislikes_count,
          c.user_id,
          c.content_id,
          cl.user_id IS NOT NULL AS alreadyLiked,
          cd.user_id IS NOT NULL AS alreadyDisliked
        FROM comments c
       LEFT JOIN comment_likes cl ON c.id = cl.comment_id AND cl.user_id = :userId
       LEFT JOIN comment_dislikes cd ON c.id = cd.comment_id AND cd.user_id = :userId
        WHERE c.content_id = :contentId
        ORDER BY
          c.likes_count DESC,
          c.dislikes_count ASC,
          c.updated_at DESC
        LIMIT :limit OFFSET :offsets;
        """
    ).param("contentId", contentId)
        .param("userId", userId)
        .param("limit", limit)
        .param("offsets", offsets)
        .query(CommentDetail.class)
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

  public void increaseLike(Long commentId, Long userId){
    jdbcClient.sql(
        """
        WITH deleted_dislike AS (
            DELETE FROM comment_dislikes
            WHERE comment_id = :commentId AND user_id = :userId
            RETURNING 1
        ),
        inserted_like AS (
            INSERT INTO comment_likes (comment_id, user_id)
            VALUES (:commentId, :userId)
            ON CONFLICT (comment_id, user_id) DO NOTHING
            RETURNING 1
        )
        UPDATE comments
        SET likes_count = COALESCE(likes_count, 0) + (SELECT COUNT(*) FROM inserted_like),
            dislikes_count = GREATEST(COALESCE(dislikes_count, 0) - (SELECT COUNT(*) FROM deleted_dislike), 0)
        WHERE id = :commentId
        """
    ).param("commentId", commentId)
        .param("userId", userId)
        .update();
  }

  public void decreaseLike(Long commentId, Long userId){
    jdbcClient.sql(
        """
        WITH deleted_like AS (
          DELETE FROM comment_likes
          WHERE comment_id = :commentId AND user_id = :userId
          RETURNING 1
        )
        UPDATE comments
            likes_count = GREATEST(COALESCE(likes_count, 0) - (SELECT COUNT(*) FROM deleted_like), 0)
        WHERE id = :commentId
        """
    ).param("commentId", commentId)
        .param("userId", userId)
        .update();
  }

  public void increaseDislike(Long commentId, Long userId){
    jdbcClient.sql(
      """
      WITH deleted_like AS (
          DELETE FROM comment_likes
          WHERE comment_id = :commentId AND user_id = :userId
          RETURNING 1
      ),
      inserted_dislike AS (
          INSERT INTO comment_dislikes (comment_id, user_id)
          VALUES (:commentId, :userId)
          ON CONFLICT (comment_id, user_id) DO NOTHING
          RETURNING 1
      )
      UPDATE comments
      SET dislikes_count = COALESCE(dislikes_count, 0) + (SELECT COUNT(*) FROM inserted_dislike),
          likes_count = GREATEST(COALESCE(likes_count, 0) - (SELECT COUNT(*) FROM deleted_like), 0)
      WHERE id = :commentId
      """
  ).param("commentId", commentId)
        .param("userId", userId)
  .update();
  }

  public void decreaseDislike(Long commentId, Long userId){
    jdbcClient.sql(
      """
      WITH deleted_dislike AS (
        DELETE FROM comment_dislikes
        WHERE comment_id = :commentId AND user_id = :userId
        RETURNING 1
      )
      UPDATE comments
          dislikes_count = GREATEST(COALESCE(dislikes_count, 0) - (SELECT COUNT(*) FROM deleted_dislike), 0)
      WHERE id = :commentId
      """
  ).param("commentId", commentId)
        .param("userId", userId)
  .update();
  }
}
