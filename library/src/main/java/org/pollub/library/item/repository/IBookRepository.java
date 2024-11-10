package org.pollub.library.item.repository;

import org.pollub.library.item.model.Book;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRepository extends ILibraryItemRepository<Book> {
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByGenre(String genre);
    List<Book> findByIsbn(String isbn);
}
