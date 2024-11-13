package org.pollub.library.item.service;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.BookNotFoundException;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.pollub.library.item.repository.IBookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements IBookService {
    private final IBookRepository bookRepository;

    @Override
    public Book createBook(BookCreateDto dto) {
        Book book = new Book();
        mapBookFromDto(book, dto);
        return saveOrThrow(book);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found."));
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public Book updateBook(Long id, BookCreateDto dto) {
        var book = findById(id);
        mapBookFromDto(book, dto);
        return saveOrThrow(book);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    private void mapBookFromDto(Book book, BookCreateDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPageCount(dto.getPageCount());
        book.setPaperType(dto.getPaperType());
        book.setPublisher(dto.getPublisher());
        book.setShelfNumber(dto.getShelfNumber());
        book.setGenre(dto.getGenre());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
    }
    private Book saveOrThrow(Book book) {
        Book savedBook = bookRepository.save(book);
        return Optional.of(savedBook)
                .orElseThrow(() -> new PersistenceException("Failed to save the book."));
    }

}
