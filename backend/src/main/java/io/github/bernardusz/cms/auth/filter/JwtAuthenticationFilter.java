package io.github.bernardusz.cms.auth.filter;

import io.github.bernardusz.cms.auth.service.CustomUserDetailsService;
import io.github.bernardusz.cms.auth.service.JwtService;
import io.github.bernardusz.cms.user.UserSecurity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
  private final HandlerExceptionResolver exceptionResolver;
  private final JwtService jwtService;
  private final CustomUserDetailsService customUserDetailsService;

  public JwtAuthenticationFilter(
    @Qualifier("handlerExceptionResolver") HandlerExceptionResolver
      exceptionResolver,
    JwtService jwtService,
    CustomUserDetailsService customUserDetailsService
  ){
    this.exceptionResolver = exceptionResolver;
    this.jwtService = jwtService;
    this.customUserDetailsService = customUserDetailsService;
  }

  @Override
  public boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.equals("/api/auth/login")
        || path.equals("/api/auth/register")
        || path.equals("/api/auth/refresh")
        || path.equals("/api/auth/logout");
  }

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException,IOException {
    String jwt = null;
    if (request.getCookies() != null){
      for (Cookie cookie : request.getCookies()){
        if ("AUTH-TOKEN".equals(cookie.getName())){
          jwt = cookie.getValue();
          break;
        }
      }
    }

    if (jwt == null){
      filterChain.doFilter(request, response);
      return;
    }

    try{
      String userId = jwtService.extractSubjectId(jwt);
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      if(userId != null && authentication == null){
        UserSecurity user = customUserDetailsService.loadUserById(userId);
        if (jwtService.validateToken(jwt, user)){
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              user, null, user.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    }
    catch (Exception e){
      exceptionResolver.resolveException(request, response, null, e);
      return;
    }
    filterChain.doFilter(request, response);

  }
}
