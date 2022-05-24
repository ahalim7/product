package org.halim.svc.user.domain.mapper;

import org.halim.svc.user.domain.dto.EditBookRequest;
import org.halim.svc.user.domain.model.Book;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = ObjectIdMapper.class)
public interface BookEditMapper {

  Book create(EditBookRequest request);

  @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
  void update(EditBookRequest request, @MappingTarget Book book);

}
