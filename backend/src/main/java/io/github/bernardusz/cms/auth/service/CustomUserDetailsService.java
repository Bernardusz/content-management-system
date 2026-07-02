package io.github.bernardusz.cms.auth.service;

import io.github.bernardusz.cms.user.UserRepository;
import io.github.bernardusz.cms.user.UserSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserSecurity loadUserByUsername(String identifier) throws UsernameNotFoundException {
    return userRepository.findByIdentifierSecurity(identifier)
      .map(UserSecurity::new)
      .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  public UserSecurity loadUserById(String userId) throws UsernameNotFoundException {
    return loadUserByUsername(userId);
  }
}
