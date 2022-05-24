package org.halim.svc.user.api;

import org.halim.svc.user.domain.dto.AuthRequest;
import org.halim.svc.user.domain.dto.CreateUserRequest;
import org.halim.svc.user.domain.dto.UserView;
import org.halim.svc.user.domain.mapper.UserViewMapper;
import org.halim.svc.user.domain.model.User;
import org.halim.svc.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.Instant;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

@Tag(name = "Authentication")
@RestController
@RequestMapping(path = "api/public")
@RequiredArgsConstructor
public class AuthApi {
  private final AuthenticationManager authenticationManager;
  private final JwtEncoder jwtEncoder;
  private final UserViewMapper userViewMapper;
  private final UserService userService;
  @PostMapping("login")
  public ResponseEntity<UserView> login(@RequestBody @Valid AuthRequest request) {
    try {
      Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

      User user = (User) authentication.getPrincipal();

      Instant now = Instant.now();
      long expiry = 36000L;

      String scope = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(joining(" "));

      JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer("org.halim.svc")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiry))
        .subject(format("%s,%s", user.getId(), user.getUsername()))
        .claim("roles", scope)
        .build();

      String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

      return ResponseEntity.ok()
        .header(HttpHeaders.AUTHORIZATION, token)
        .body(userViewMapper.toUserView(user));
    } catch (BadCredentialsException ex) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("register")
  public UserView register(@RequestBody @Valid CreateUserRequest request) {
    return userService.create(request);
  }

}
