package io.github.bernardusz.cms.auth;

import io.github.bernardusz.cms.auth.dto.LoginRequest;
import io.github.bernardusz.cms.auth.dto.LoginResponse;
import io.github.bernardusz.cms.user.UserSecurity;
import io.github.bernardusz.cms.user.UserService;
import io.github.bernardusz.cms.user.dto.UserCreation;
import io.github.bernardusz.cms.user.dto.UserDetail;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;
  private final UserService userService;

  public AuthController(AuthService authService, UserService userService) {
    this.authService = authService;
    this.userService = userService;
  }

  @GetMapping("/me")
  public ResponseEntity<UserDetail> getUser(@AuthenticationPrincipal UserSecurity user) {
    String username = user.getUsername() == null ? "anonymousUser" : user.getUsername();

    return user == null || "anonymousUser".equals(username) ?
      ResponseEntity.ok().build() :
      ResponseEntity.ok(userService.findById(user ,user.getId()));
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @RequestBody LoginRequest loginRequest,
      HttpServletResponse response
  ) {
    LoginResponse loginResponse = authService.loginUser(loginRequest);

    ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", loginResponse.accessToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(loginResponse.expiresIn() / 1000)
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", loginResponse.refreshToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(loginResponse.refreshTokenExpiresIn() / 1000)
        .sameSite("Strict")
        .build();

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody UserCreation registerRequest) {
    authService.registerUser(registerRequest);
    return ResponseEntity.ok("User registered successfully");
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    String refreshToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("REFRESH-TOKEN".equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    if (refreshToken == null) {
      return ResponseEntity.badRequest().build();
    }

    LoginResponse loginResponse = authService.refreshAccessToken(refreshToken);

    ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", loginResponse.accessToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(loginResponse.expiresIn() / 1000)
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", loginResponse.refreshToken())
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(loginResponse.refreshTokenExpiresIn() / 1000)
        .sameSite("Strict")
        .build();

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());

    return ResponseEntity.ok().build();
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(@AuthenticationPrincipal UserSecurity user, HttpServletResponse response) {
    ResponseCookie accessTokenCookie = ResponseCookie.from("AUTH-TOKEN", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("REFRESH-TOKEN", "")
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();

    response.addHeader("Set-Cookie", accessTokenCookie.toString());
    response.addHeader("Set-Cookie", refreshTokenCookie.toString());

    return ResponseEntity.ok("Logout successful");
  }

}
