package io.github.bernardusz.cms.user;

import io.github.bernardusz.cms.user.dto.UserDetail;
import io.github.bernardusz.cms.user.dto.UserUpdateInformation;
import io.github.bernardusz.cms.user.dto.UserUpdatePassword;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
  private final UserService userService;
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/user/{userId}")
  public UserDetail findById(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId) {
    return userService.findById(user ,userId);
  }

  @PutMapping("/user/{userId}/information")
  public void updateInformation(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId, @RequestBody UserUpdateInformation userUpdateInformation) {
    userService.updateInformation(user, userId, userUpdateInformation);
  }

  @PutMapping("/user/{userId}/password")
  public void updatePassword(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId, @RequestBody UserUpdatePassword userUpdatePassword) {
    userService.updatePassword(user, userId, userUpdatePassword);
  }

  @DeleteMapping("/user/{userId}")
  public void deleteById(@AuthenticationPrincipal UserSecurity user, @PathVariable Long userId) {
    userService.deleteById(user, userId);
  }
}
