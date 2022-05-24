package org.halim.svc.user.domain.mapper;

import org.halim.svc.user.domain.dto.EditAuthorRequest;
import org.halim.svc.user.domain.model.Author;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public interface AuthorEditMapper {

  Author create(EditAuthorRequest request);

  @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
  void update(EditAuthorRequest request, @MappingTarget Author author);

}
