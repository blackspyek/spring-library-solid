package org.pollub.library.item.service;

import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.dto.BookCreateDto;

import java.util.List;

public interface IBookService {
    Book createBook(BookCreateDto dto);
    Book findById(Long id);
    List<Book> findByAuthor(String author);
    List<Book> findByGenre(String genre);
    Book updateBook(Long id, BookCreateDto dto);
    void deleteBook(Long id);
    List<Book> findByIsbn(String isbn);
}
