package io.github.bernardusz.cms.user;

import io.github.bernardusz.cms.exception.exceptions.NotAuthorizedException;
import io.github.bernardusz.cms.exception.exceptions.UserNotFound;
import io.github.bernardusz.cms.user.dto.UserDetail;
import io.github.bernardusz.cms.user.dto.UserUpdateInformation;
import io.github.bernardusz.cms.user.dto.UserUpdatePassword;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public Optional<User> findByIdentifierSecurity(String identifier){
    return userRepository.findByIdentifierSecurity(identifier);
  }

  @Transactional(readOnly = true)
  public UserDetail findById(UserSecurity user, Long userId){
    if (userId != user.getId()){
      throw new NotAuthorizedException(
        "You are not authorized to look into this endpoint"
      );
    }
    return userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFound("User doesn't exist"));
  }

  public void updateInformation(UserSecurity user, Long userId, UserUpdateInformation userUpdateInformation){
    if (userId != user.getId()){
      throw new NotAuthorizedException(
        "You are not authorized to look into this endpoint"
      );
    }
    userRepository.updateInformation(userId, userUpdateInformation);
  }

  public void updatePassword(UserSecurity user, Long userId, UserUpdatePassword userUpdatePassword){
    if (userId != user.getId()){
      throw new NotAuthorizedException(
        "You are not authorized to look into this endpoint"
      );
    }
    userRepository.updatePassword(userId, userUpdatePassword);
  }

  public void deleteById(UserSecurity user,Long userId){
    if (userId != user.getId()){
      throw new NotAuthorizedException(
        "You are not authorized to look into this endpoint"
      );
    }
    userRepository.deleteById(userId);
  }
}
