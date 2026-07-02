package io.github.bernardusz.cms.auth;

import io.github.bernardusz.cms.auth.dto.RefreshTokenInfo;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class AuthRepository {
  private final JdbcClient jdbcClient;

  public AuthRepository(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public void saveRefreshToken(Long userId, String tokenHash, String salt, long expiresIn) {
    Instant expiresAt = Instant.now().plusMillis(expiresIn);
    jdbcClient.sql("""
      INSERT INTO refresh_tokens (user_id, token_hash, salt, expires_at)
      VALUES (:userId, :tokenHash, :salt, :expiresAt)
      """)
      .param("userId", userId)
      .param("tokenHash", tokenHash)
      .param("salt", salt)
      .param("expiresAt", expiresAt)
      .update();
  }

  public Optional<RefreshTokenInfo> findRefreshTokenByUserId(Long userId) {
    return jdbcClient.sql("""
      SELECT id, user_id, token_hash, salt, expires_at
      FROM refresh_tokens
      WHERE user_id = :userId
      ORDER BY expires_at DESC
      LIMIT 1
      """)
      .param("userId", userId)
      .query((rs, rowNum) -> new RefreshTokenInfo(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getString("token_hash"),
        rs.getString("salt"),
        rs.getTimestamp("expires_at").toInstant()
      ))
      .optional();
  }

  public Optional<RefreshTokenInfo> findRefreshToken(String tokenHash) {
    return jdbcClient.sql("""
      SELECT id, user_id, token_hash, salt, expires_at
      FROM refresh_tokens
      WHERE token_hash = :tokenHash
      """)
      .param("tokenHash", tokenHash)
      .query((rs, rowNum) -> new RefreshTokenInfo(
        rs.getLong("id"),
        rs.getLong("user_id"),
        rs.getString("token_hash"),
        rs.getString("salt"),
        rs.getTimestamp("expires_at").toInstant()
      ))
      .optional();
  }

  public void deleteRefreshToken(String tokenHash) {
    jdbcClient.sql("DELETE FROM refresh_tokens WHERE token_hash = :tokenHash")
      .param("tokenHash", tokenHash)
      .update();
  }

  public void deleteExpiredTokens() {
    jdbcClient.sql("DELETE FROM refresh_tokens WHERE expires_at < :now")
      .param("now", Instant.now())
      .update();
  }


}
