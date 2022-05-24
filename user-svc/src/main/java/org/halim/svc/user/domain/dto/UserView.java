package org.halim.svc.user.domain.dto;

public record UserView(
  String id,

  String username,
  String fullName
) {

}
