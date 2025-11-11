package org.pollub.library.item.service;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.exception.BookNotFoundException;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.pollub.library.item.repository.IBookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements IBookService {
    private final IBookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> getBooksPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return bookRepository.findAll(pageable);
    }

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

    @Override
    public Page<Book> searchBooks(String query, ItemStatus status, String publisher, String genres, int page, int size, String sort) {

        Sort sortSpec;
        if ("TITLE_ASC".equals(sort)) {
            sortSpec = Sort.by("title").ascending();
        } else if ("TITLE_DESC".equals(sort)) {
            sortSpec = Sort.by("title").descending();
        } else if ("AUTHOR_ASC".equals(sort)) {
            sortSpec = Sort.by("author").ascending();
        } else if ("AUTHOR_DESC".equals(sort)) {
            sortSpec = Sort.by("author").descending();
        } else {
            sortSpec = Sort.by("id").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sortSpec);

        String queryParam = (query != null && !query.isBlank()) ? query : null;
        String publisherParam = (publisher != null && !publisher.isBlank()) ? publisher : null;
        String genresParam = (genres != null && !genres.isEmpty()) ? genres : null;

        return bookRepository.searchBooks(queryParam, status, publisherParam, genresParam, pageable);
    }

    @Override
    public List<String> getTopGenres() {
        return bookRepository.findTop4Genres();
    }

    @Override
    public List<String> getOtherGenres() {
        return bookRepository.findOtherGenres();
    }

    @Override
    public List<String> getAllPublishers() {
        return bookRepository.findAllPublishers();
    }

    @Override
    public List<String> getAllStatuses() {
        return Arrays.stream(ItemStatus.values())
                .map(Enum::name)
                .sorted()
                .collect(Collectors.toList());
    }
}
