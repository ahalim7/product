package org.halim.svc.user.api;

import org.halim.svc.user.domain.dto.AuthorView;
import org.halim.svc.user.domain.dto.BookView;
import org.halim.svc.user.domain.dto.EditAuthorRequest;
import org.halim.svc.user.domain.dto.ListResponse;
import org.halim.svc.user.domain.dto.SearchAuthorsQuery;
import org.halim.svc.user.domain.dto.SearchRequest;
import org.halim.svc.user.domain.model.Role;
import org.halim.svc.user.service.AuthorService;
import org.halim.svc.user.service.BookService;
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

@Tag(name = "Author")
@RestController
@RequestMapping(path = "api/author")
@RequiredArgsConstructor
public class AuthorApi {

  private final AuthorService authorService;
  private final BookService bookService;

  @RolesAllowed(Role.AUTHOR_ADMIN)
  @PostMapping
  public AuthorView create(@RequestBody @Valid EditAuthorRequest request) {
    return authorService.create(request);
  }

  @RolesAllowed(Role.AUTHOR_ADMIN)
  @PutMapping("{id}")
  public AuthorView edit(@PathVariable String id, @RequestBody @Valid EditAuthorRequest request) {
    return authorService.update(new ObjectId(id), request);
  }

  @RolesAllowed(Role.AUTHOR_ADMIN)
  @DeleteMapping("{id}")
  public AuthorView delete(@PathVariable String id) {
    return authorService.delete(new ObjectId(id));
  }

  @GetMapping("{id}")
  public AuthorView get(@PathVariable String id) {
    return authorService.getAuthor(new ObjectId(id));
  }

  @GetMapping("{id}/book")
  public ListResponse<BookView> getBooks(@PathVariable String id) {
    return new ListResponse<>(bookService.getAuthorBooks(new ObjectId(id)));
  }

  @PostMapping("search")
  public ListResponse<AuthorView> search(@RequestBody @Valid SearchRequest<SearchAuthorsQuery> request) {
    return new ListResponse<AuthorView>(authorService.searchAuthors(request.page(), request.query()));
  }

}
