package org.halim.svc.user.api;

import org.halim.svc.user.domain.dto.CreateUserRequest;
import org.halim.svc.user.domain.dto.ListResponse;
import org.halim.svc.user.domain.dto.SearchRequest;
import org.halim.svc.user.domain.dto.SearchUsersQuery;
import org.halim.svc.user.domain.dto.UpdateUserRequest;
import org.halim.svc.user.domain.dto.UserView;
import org.halim.svc.user.domain.model.Role;
import org.halim.svc.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Tag(name = "UserAdmin")
@RestController
@RequestMapping(path = "api/admin/user")
@RolesAllowed(Role.USER_ADMIN)
@RequiredArgsConstructor
public class UserAdminApi {

  private final UserService userService;

  @PostMapping
  public UserView create(@RequestBody @Valid CreateUserRequest request) {
    return userService.create(request);
  }

  @PutMapping("{id}")
  public UserView update(@PathVariable String id, @RequestBody @Valid UpdateUserRequest request) {
    return userService.update(new ObjectId(id), request);
  }

  @DeleteMapping("{id}")
  public UserView delete(@PathVariable String id) {
    return userService.delete(new ObjectId(id));
  }

  @GetMapping("{id}")
  public UserView get(@PathVariable String id) {
    return userService.getUser(new ObjectId(id));
  }

  @PostMapping("search")
  public ListResponse<UserView> search(@RequestBody SearchRequest<SearchUsersQuery> request) {
    return new ListResponse<UserView>(userService.searchUsers(request.page(), request.query()));
  }

}
