package org.pollub.catalog.proxy;

import lombok.extern.slf4j.Slf4j;
import org.pollub.catalog.cache.CatalogCacheManager;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.SearchCriteria;
import org.pollub.catalog.model.dto.BookAvailabilityDto;
import org.pollub.catalog.model.dto.BookCreateDto;
import org.pollub.catalog.service.IBookService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Caching Proxy (wzorzec Proxy) dla IBookService.
 * Dodaje warstwę cachowania dla kosztownych operacji odczytu (gatunki, wydawcy),
 * delegując resztę operacji do prawdziwego BookService.
 */
//Lab1 - Proxy 1 Start
@Component
@Primary
@Slf4j
public class CachingBookServiceProxy implements IBookService {

    private final IBookService bookService;

    public CachingBookServiceProxy(@Qualifier("bookService") IBookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public List<String> getTopGenres() {
        List<String> cached = CatalogCacheManager.INSTANCE.get("topGenres", List.class);
        if (cached != null) {
            log.debug("Cache hit for topGenres");
            return cached;
        }
        log.debug("Cache miss for topGenres, fetching from service");
        List<String> genres = bookService.getTopGenres();
        CatalogCacheManager.INSTANCE.put("topGenres", genres);
        return genres;
    }

    @Override
    public List<String> getOtherGenres() {
        List<String> cached = CatalogCacheManager.INSTANCE.get("otherGenres", List.class);
        if (cached != null) {
            log.debug("Cache hit for otherGenres");
            return cached;
        }
        log.debug("Cache miss for otherGenres, fetching from service");
        List<String> genres = bookService.getOtherGenres();
        CatalogCacheManager.INSTANCE.put("otherGenres", genres);
        return genres;
    }

    @Override
    public List<String> getAllPublishers() {
        List<String> cached = CatalogCacheManager.INSTANCE.get("allPublishers", List.class);
        if (cached != null) {
            log.debug("Cache hit for allPublishers");
            return cached;
        }
        log.debug("Cache miss for allPublishers, fetching from service");
        List<String> publishers = bookService.getAllPublishers();
        CatalogCacheManager.INSTANCE.put("allPublishers", publishers);
        return publishers;
    }

    @Override
    public List<Book> findAll() {
        return bookService.findAll();
    }

    @Override
    public Page<Book> getBooksPaginated(int page, int size) {
        return bookService.getBooksPaginated(page, size);
    }

    @Override
    public Book createBook(BookCreateDto dto) {
        return bookService.createBook(dto);
    }

    @Override
    public Book findById(Long id) {
        return bookService.findById(id);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookService.findByAuthor(author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookService.findByTitle(title);
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return bookService.findByGenre(genre);
    }

    @Override
    public Book updateBook(Long id, BookCreateDto dto) {
        return bookService.updateBook(id, dto);
    }

    @Override
    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return bookService.findByIsbn(isbn);
    }

    @Override
    public Page<Book> searchBooks(SearchCriteria criteria) {
        return bookService.searchBooks(criteria);
    }

    @Override
    public List<String> getAllStatuses() {
        return bookService.getAllStatuses();
    }

    @Override
    public List<Book> getRecentBooks(int limit) {
        return bookService.getRecentBooks(limit);
    }

    @Override
    public List<Book> getPopularBooks(int limit) {
        return bookService.getPopularBooks(limit);
    }

    @Override
    public BookAvailabilityDto getBookAvailability(Long id) {
        return bookService.getBookAvailability(id);
    }
}
//Lab1 - Proxy 1 End
