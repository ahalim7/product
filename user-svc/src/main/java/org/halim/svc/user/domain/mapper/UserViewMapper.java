package org.halim.svc.user.domain.mapper;

import org.halim.svc.user.domain.dto.UserView;
import org.halim.svc.user.domain.model.User;
import org.halim.svc.user.repository.UserRepo;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ObjectIdMapper.class})
public abstract class UserViewMapper {

  @Autowired
  private UserRepo userRepo;

  public abstract UserView toUserView(User user);

  public abstract List<UserView> toUserView(List<User> users);

  public UserView toUserViewById(ObjectId id) {
    if (id == null) {
      return null;
    }
    return toUserView(userRepo.getById(id));
  }

}
