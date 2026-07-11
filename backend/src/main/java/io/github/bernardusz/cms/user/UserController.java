package io.github.bernardusz.cms.user;

import io.github.bernardusz.cms.user.dto.UserDetail;
import io.github.bernardusz.cms.user.dto.UserUpdateInformation;
import io.github.bernardusz.cms.user.dto.UserUpdatePassword;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PutMapping("/{userId}/information")
  public void updateInformation(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId, @RequestBody UserUpdateInformation userUpdateInformation) {
    userService.updateInformation(user, userId, userUpdateInformation);
  }

  @PutMapping("/{userId}/password")
  public void updatePassword(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId, @RequestBody UserUpdatePassword userUpdatePassword) {
    userService.updatePassword(user, userId, userUpdatePassword);
  }

  @DeleteMapping("/{userId}")
  public void deleteById(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId) {
    userService.deleteById(user, userId);
  }
}
