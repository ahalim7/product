package org.halim.svc.user.domain.mapper;

import org.halim.svc.user.domain.dto.CreateUserRequest;
import org.halim.svc.user.domain.dto.UpdateUserRequest;
import org.halim.svc.user.domain.model.Role;
import org.halim.svc.user.domain.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public abstract class UserEditMapper {

  @Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
  public abstract User create(CreateUserRequest request);

  @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
  @Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
  public abstract void update(UpdateUserRequest request, @MappingTarget User user);

  @Named("stringToRole")
  protected Set<Role> stringToRole(Set<String> authorities) {
    if (authorities != null) {
      return authorities.stream().map(Role::new).collect(toSet());
    }
    return new HashSet<>();
  }

}
