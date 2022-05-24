package org.halim.svc.user.service;

import org.halim.svc.user.domain.dto.AuthorView;
import org.halim.svc.user.domain.dto.EditAuthorRequest;
import org.halim.svc.user.domain.dto.Page;
import org.halim.svc.user.domain.dto.SearchAuthorsQuery;
import org.halim.svc.user.domain.mapper.AuthorEditMapper;
import org.halim.svc.user.domain.mapper.AuthorViewMapper;
import org.halim.svc.user.domain.model.Author;
import org.halim.svc.user.domain.model.Book;
import org.halim.svc.user.repository.AuthorRepo;
import org.halim.svc.user.repository.BookRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

  private final AuthorRepo authorRepo;
  private final BookRepo bookRepo;
  private final AuthorEditMapper authorEditMapper;
  private final AuthorViewMapper authorViewMapper;

  @Transactional
  public AuthorView create(EditAuthorRequest request) {
    Author author = authorEditMapper.create(request);

    author = authorRepo.save(author);

    return authorViewMapper.toAuthorView(author);
  }

  @Transactional
  public AuthorView update(ObjectId id, EditAuthorRequest request) {
    Author author = authorRepo.getById(id);
    authorEditMapper.update(request, author);

    author = authorRepo.save(author);

    return authorViewMapper.toAuthorView(author);
  }

  @Transactional
  public AuthorView delete(ObjectId id) {
    Author author = authorRepo.getById(id);

    authorRepo.delete(author);
    bookRepo.deleteAll(bookRepo.findAllById(author.getBookIds()));

    return authorViewMapper.toAuthorView(author);
  }

  public AuthorView getAuthor(ObjectId id) {
    return authorViewMapper.toAuthorView(authorRepo.getById(id));
  }

  public List<AuthorView> getAuthors(Iterable<ObjectId> ids) {
    return authorViewMapper.toAuthorView(authorRepo.findAllById(ids));
  }

  public List<AuthorView> getBookAuthors(ObjectId bookId) {
    Book book = bookRepo.getById(bookId);
    return authorViewMapper.toAuthorView(authorRepo.findAllById(book.getAuthorIds()));
  }

  public List<AuthorView> searchAuthors(Page page, SearchAuthorsQuery query) {
    return authorViewMapper.toAuthorView(authorRepo.searchAuthors(page, query));
  }

}