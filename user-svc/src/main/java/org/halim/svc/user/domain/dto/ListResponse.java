package org.halim.svc.user.domain.dto;

import java.util.List;

public record ListResponse<T>(
  List<T> items
) {

  public ListResponse {
  }

  public ListResponse() {
    this(List.of());
  }
}
