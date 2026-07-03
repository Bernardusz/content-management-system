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

  public List<ContentSummary> findAllWithFilter(String identifier, int limit, int offsets){
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
        WHERE whereClause IS NULL OR title ILIKE :whereClause OR description ILIKE :whereClause
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
        .query(ContentSummary.class)
        .list();
  }

  public Optional<ContentDetail> findById(Long id){
    return jdbcClient.sql(
        """
        SELECT
          id,
          title,
          description,
          content,
          created_at,
          updated_at,
          comments_count,
          likes_count,
          dislikes_count,
          user_id
        FROM contents
        WHERE id = :id
        """
    ).param("id", id)
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

  public void increaseLike(Long id){
    jdbcClient.sql(
        """
        UPDATE contents SET
        SET likes_count = COALESCE(likes_count, 0) + 1
        WHERE id = :id
        """
    ).param("id", id)
        .update();
  }

  public void decreaseLike(Long id){
    jdbcClient.sql(
        """
        UPDATE contents SET
        SET likes_count = COALESCE(likes_count, 0) - 1
        WHERE id = :id
        """
    ).param("id", id)
    .update();
  }

  public void increaseDislike(Long id){
    jdbcClient.sql(
      """
      UPDATE contents SET
      SET dislikes_count = COALESCE(dislikes_count, 0) + 1
      WHERE id = :id
      """
  ).param("id", id)
  .update();
  }

  public void decreaseDislike(Long id){
    jdbcClient.sql(
      """
      UPDATE contents SET
      SET dislikes_count = COALESCE(dislikes_count, 0) - 1
      WHERE id = :id
      """
  ).param("id", id)
  .update();
  }

}
