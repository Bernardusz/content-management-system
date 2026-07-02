package io.github.bernardusz.cms.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public record UserSecurity(
  User user
) implements UserDetails {
  public Long getId() {
    return user.id();
  }

  public String getEmail() {
    return user.email();
  }

  @Override
  public String getUsername() {
    return user.username(); // Your email acts as the login username
  }

  @Override
  public String getPassword() {
    return user.password();
  }

  public LocalDateTime getCreatedAt(){
    return user.createdAt();
  }


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}
