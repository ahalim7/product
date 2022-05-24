package org.halim.svc.user.service;

import org.halim.svc.user.domain.dto.CreateUserRequest;
import org.halim.svc.user.domain.dto.Page;
import org.halim.svc.user.domain.dto.SearchUsersQuery;
import org.halim.svc.user.domain.dto.UpdateUserRequest;
import org.halim.svc.user.domain.dto.UserView;
import org.halim.svc.user.domain.mapper.UserEditMapper;
import org.halim.svc.user.domain.mapper.UserViewMapper;
import org.halim.svc.user.domain.model.User;
import org.halim.svc.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepo userRepo;
  private final UserEditMapper userEditMapper;
  private final UserViewMapper userViewMapper;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public UserView create(CreateUserRequest request) {
    if (userRepo.findByUsername(request.username()).isPresent()) {
      throw new ValidationException("Username exists!");
    }
    if (!request.password().equals(request.rePassword())) {
      throw new ValidationException("Passwords don't match!");
    }

    User user = userEditMapper.create(request);
    user.setPassword(passwordEncoder.encode(request.password()));

    user = userRepo.save(user);

    return userViewMapper.toUserView(user);
  }

  @Transactional
  public UserView update(ObjectId id, UpdateUserRequest request) {
    User user = userRepo.getById(id);
    userEditMapper.update(request, user);

    user = userRepo.save(user);

    return userViewMapper.toUserView(user);
  }

  @Transactional
  public UserView upsert(CreateUserRequest request) {
    Optional<User> optionalUser = userRepo.findByUsername(request.username());

    if (optionalUser.isEmpty()) {
      return create(request);
    } else {
      UpdateUserRequest updateUserRequest = new UpdateUserRequest(
        request.fullName(), request.authorities()
      );
      return update(optionalUser.get().getId(), updateUserRequest);
    }
  }

  @Transactional
  public UserView delete(ObjectId id) {
    User user = userRepo.getById(id);

    user.setUsername(user.getUsername().replace("@", String.format("_%s@", user.getId().toString())));
    user.setEnabled(false);
    user = userRepo.save(user);

    return userViewMapper.toUserView(user);
  }

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepo
      .findByUsername(username)
      .orElseThrow(
        () -> new UsernameNotFoundException(format("User with username - %s, not found", username)));
  }

  public boolean usernameExists(String username) {
    return userRepo.findByUsername(username).isPresent();
  }

  public UserView getUser(ObjectId id) {
    return userViewMapper.toUserView(userRepo.getById(id));
  }

  public List<UserView> searchUsers(Page page, SearchUsersQuery query) {
    List<User> users = userRepo.searchUsers(page, query);
    return userViewMapper.toUserView(users);
  }

}
