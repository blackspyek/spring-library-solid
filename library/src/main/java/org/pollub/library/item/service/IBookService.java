package org.pollub.library.item.service;

import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IBookService {

    List<Book> findAll();
    Page<Book> getBooksPaginated(int page, int size);
    Book createBook(BookCreateDto dto);
    Book findById(Long id);
    List<Book> findByAuthor(String author);
    List<Book> findByTitle(String title);
    List<Book> findByGenre(String genre);
    Book updateBook(Long id, BookCreateDto dto);
    void deleteBook(Long id);
    List<Book> findByIsbn(String isbn);
    Page<Book> searchBooks(String query, ItemStatus status, String publisher, String genres, int page, int size, String sort);
    List<String> getTopGenres();
    List<String> getOtherGenres();
    List<String> getAllPublishers();
    List<String> getAllStatuses();

}
