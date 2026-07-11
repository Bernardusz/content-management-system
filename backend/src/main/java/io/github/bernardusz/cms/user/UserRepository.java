package io.github.bernardusz.cms.user;

import io.github.bernardusz.cms.user.dto.UserCreation;
import io.github.bernardusz.cms.user.dto.UserDetail;
import io.github.bernardusz.cms.user.dto.UserUpdateInformation;
import io.github.bernardusz.cms.user.dto.UserUpdatePassword;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepository {
  private final JdbcClient jdbcClient;
  public UserRepository(JdbcClient jdbcClient){
    this.jdbcClient = jdbcClient;
  }

  public boolean existsByUsername(String username){
    return jdbcClient.sql(
        """
        SELECT EXISTS (
          SELECT 1 FROM users WHERE username = :username
        )
        """
    ).param("username", username).query(Boolean.class).single();
  }
  public Optional<User> findByIdentifierSecurity(String identifier){
    return jdbcClient.sql(
        """
        SELECT * FROM users
        WHERE
          username = :identifier OR
          email = :identifier
        """
      ).param("identifier", identifier)
      .query(User.class)
      .optional();
  }

  public Optional<User> findByIdSecurity(Long userId){
    return jdbcClient.sql(
      """
      SELECT * FROM users
      WHERE id = :userId    
      """
    ).param("userId", userId).query(User.class).optional();
  }

  public Optional<User> findUserByRefreshTokenSecurity(String tokenHash){
    return jdbcClient.sql(
        """
        SELECT
          u.id,
          u.username,
          u.email,
          u.password,
          u.created_at
        FROM users u
        INNER JOIN refresh_tokens rt
        ON rt.user_id = u.id
        WHERE token_hash = :tokenHash
        """
    ).param("tokenHash", tokenHash).query(User.class).optional();
  }

  public Optional<Long> save(UserCreation userCreation){
    return jdbcClient.sql(
      """
        INSERT INTO users (username, email, password)
        VALUES(:username, :email, :password)
        RETURNING id
      """
    ).param("username", userCreation.username())
      .param("email", userCreation.email())
      .param("password", userCreation.password())
      .query(Long.class)
      .optional();
  }

  public Optional<UserDetail> findById(Long userId){
    return jdbcClient.sql(
      """
      SELECT id, username, email, created_at
      FROM users WHERE id = :userId
      """
    ).param("userId", userId)
      .query(UserDetail.class)
      .optional();
  }

  public void updateInformation(Long userId, UserUpdateInformation userUpdateInformation){
    jdbcClient.sql(
      """
      UPDATE users SET
        username = :username
        email = :email
      WHERE id = :userId
      """
    ).param("username", userUpdateInformation.username())
      .param("email", userUpdateInformation.email())
      .param("userId", userId)
      .update();
  }

  public void updatePassword(Long userId, UserUpdatePassword password){
    jdbcClient.sql(
      """
      UPDATE users SET
        password = :password
      WHERE id = :userId
      """
    ).param("password", password.password())
      .param("userId", userId)
      .update();
  }

  public void deleteById(Long userId){
    jdbcClient.sql(
      """
      DELETE FROM users
      WHERE id = :userId
      """
    ).param("userId", userId)
      .update();
  }
}
