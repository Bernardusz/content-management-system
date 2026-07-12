package io.github.bernardusz.cms.content;

import io.github.bernardusz.cms.content.dto.ContentCreation;
import io.github.bernardusz.cms.content.dto.ContentDetail;
import io.github.bernardusz.cms.content.dto.ContentSummary;
import io.github.bernardusz.cms.content.dto.ContentUpdate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ContentRepository {
  private final JdbcClient jdbcClient;
  public ContentRepository(JdbcClient jdbcClient){
    this.jdbcClient = jdbcClient;
  }

  public Optional<Long> save(ContentCreation contentCreation){
    return jdbcClient.sql(
        """
        INSERT INTO contents
        (title, description, content, is_private, user_id, created_at, updated_at)
        VALUES
        (:title, :description, :content, :isPrivate, :userId, NOW(), NOW())
        RETURNING id
        """
    ).param("title", contentCreation.title())
        .param("description", contentCreation.description())
        .param("content", contentCreation.content())
        .param("isPrivate", contentCreation.isPrivate())
        .param("userId", contentCreation.userId())
        .query(Long.class)
        .optional();
  }

  public List<ContentSummary> findAllWithFilter(Long userId, String identifier, int limit, int offsets){
    String whereClause = identifier != null ? "%" + identifier + "%" : null;
    return jdbcClient.sql(
        """
        SELECT 
          id,
          title,
          description,
          created_at,
          updated_at,
          comments_count,
          likes_count,
          dislikes_count,
          user_id
        FROM contents
        WHERE (:whereClause::text IS NULL OR title ILIKE :whereClause OR description ILIKE :whereClause) AND
        (is_private = false OR user_id = :userId)
        ORDER BY
          likes_count DESC,
          comments_count DESC,
          dislikes_count ASC,
          updated_at DESC
        LIMIT :limit OFFSET :offsets;
        """
    ).param("whereClause", whereClause)
        .param("limit", limit)
        .param("offsets", offsets)
        .param("userId", userId)
        .query(ContentSummary.class)
        .list();
  }

  public Optional<ContentDetail> findById(Long id, Long userId){
    return jdbcClient.sql(
        """
        SELECT
          c.id,
          c.title,
          c.description,
          c.content,
          c.created_at,
          c.updated_at,
          c.comments_count,
          c.likes_count,
          c.dislikes_count,
          c.user_id,
          cl.user_id IS NOT NULL AS alreadyLiked,
          cd.user_id IS NOT NULL AS alreadyDisliked
        FROM contents c
        LEFT JOIN content_likes cl ON c.id = cl.content_id AND cl.user_id = :userId
        LEFT JOIN content_dislikes cd ON c.id = cd.content_id AND cd.user_id = :userId
        WHERE c.id = :id
        """
    ).param("id", id)
        .param("userId", userId)
        .query(ContentDetail.class)
        .optional();
  }

  public void updateById(Long id, ContentUpdate contentUpdate){
    jdbcClient.sql(
        """
        UPDATE contents SET
          title = :title,
          description = :description,
          content = :content,
          is_private = :isPrivate,
          updated_at = NOW()
        WHERE id = :id
        """
    ).param("title", contentUpdate.title())
        .param("description", contentUpdate.description())
        .param("content", contentUpdate.content())
        .param("isPrivate", contentUpdate.isPrivate())
        .param("id", id)
        .update();
  }

  public void deleteById(Long id){
    jdbcClient.sql(
        """
        DELETE FROM contents
        WHERE id = :id
        """
    ).param("id", id)
        .update();
  }

  public void increaseLike(Long contentId, Long userId){
    jdbcClient.sql(
        """
        WITH deleted_dislike AS (
            DELETE FROM content_dislikes
            WHERE content_id = :contentId AND user_id = :userId
            RETURNING 1
        ),
        inserted_like AS (
            INSERT INTO content_likes (content_id, user_id)
            VALUES (:contentId, :userId)
            ON CONFLICT (content_id, user_id) DO NOTHING
            RETURNING 1
        )
        UPDATE contents
        SET likes_count = COALESCE(likes_count, 0) + (SELECT COUNT(*) FROM inserted_like),
            dislikes_count = GREATEST(COALESCE(dislikes_count, 0) - (SELECT COUNT(*) FROM deleted_dislike), 0)
        WHERE id = :contentId
        """
    ).param("contentId", contentId)
        .param("userId", userId)
        .update();
  }

  public void decreaseLike(Long contentId, Long userId){
    jdbcClient.sql(
        """
        WITH deleted_like AS (
          DELETE FROM content_likes
          WHERE content_id = :contentId AND user_id = :userId
          RETURNING 1
        )
        UPDATE contents
            likes_count = GREATEST(COALESCE(likes_count, 0) - (SELECT COUNT(*) FROM deleted_like), 0)
        WHERE id = :contentId
        """
    ).param("contentId", contentId)
        .param("userId", userId)
    .update();
  }

  public void increaseDislike(Long contentId, Long userId){
    jdbcClient.sql(
      """
      WITH deleted_like AS (
          DELETE FROM content_likes
          WHERE content_id = :contentId AND user_id = :userId
          RETURNING 1
      ),
      inserted_dislike AS (
          INSERT INTO content_dislikes (content_id, user_id)
          VALUES (:contentId, :userId)
          ON CONFLICT (content_id, user_id) DO NOTHING
          RETURNING 1
      )
      UPDATE contents
      SET dislikes_count = COALESCE(dislikes_count, 0) + (SELECT COUNT(*) FROM inserted_dislike),
          likes_count = GREATEST(COALESCE(likes_count, 0) - (SELECT COUNT(*) FROM deleted_like), 0)
      WHERE id = :contentId
      """
  ).param("contentId", contentId)
        .param("userId", userId)
  .update();
  }

  public void decreaseDislike(Long contentId, Long userId){
    jdbcClient.sql(
      """
      WITH deleted_dislike AS (
        DELETE FROM content_dislikes
        WHERE content_id = :contentId AND user_id = :userId
        RETURNING 1
      )
      UPDATE contents
          dislikes_count = GREATEST(COALESCE(dislikes_count, 0) - (SELECT COUNT(*) FROM deleted_dislike), 0)
      WHERE id = :contentId
      """
  ).param("contentId", contentId)
        .param("userId", userId)
  .update();
  }

}
