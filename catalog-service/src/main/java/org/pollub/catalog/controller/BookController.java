package org.pollub.catalog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.ItemStatus;
import org.pollub.catalog.model.dto.BookAvailabilityDto;
import org.pollub.catalog.model.dto.BookCreateDto;
import org.pollub.catalog.service.IBookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items/book")
@RequiredArgsConstructor
public class BookController {
    private final IBookService bookService;
    private final Logger log = LoggerFactory.getLogger(BookController.class);

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/pagination")
    public Page<Book> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size) {

        if (size > 16) {
            size = 16;
        }
        return bookService.getBooksPaginated(page, size);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookCreateDto dto) {
        return ResponseEntity.ok(bookService.createBook(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.findByAuthor(author));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.findByTitle(title));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.findByGenre(genre));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookCreateDto dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String genres,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(required = false) String sort
    ) {
        if (size > 16) {
            size = 16;
        }
        this.log.debug("Searching books with query: {}, status: {}, publisher: {}, genres: {}, page: {}, size: {}, sort: {}",
                query, status, publisher, genres, page, size, sort);
        return ResponseEntity.ok(bookService.searchBooks(query, status, publisher, genres, page, size, sort));
    }
    @GetMapping("/genres")
    public ResponseEntity<List<String>> getTopGenres() {
        return ResponseEntity.ok(bookService.getTopGenres());
    }

    @GetMapping("/genres/other")
    public ResponseEntity<List<String>> getOtherGenres() {
        return ResponseEntity.ok(bookService.getOtherGenres());
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<String>> getAllPublishers() {
        return ResponseEntity.ok(bookService.getAllPublishers());
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> getAllStatuses() {
        return ResponseEntity.ok(bookService.getAllStatuses());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Book>> getRecentBooks(
            @RequestParam(defaultValue = "7") int limit) {
        if (limit > 20) {
            limit = 20;
        }
        return ResponseEntity.ok(bookService.getRecentBooks(limit));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Book>> getPopularBooks(
            @RequestParam(defaultValue = "10") int limit) {
        if (limit > 20) {
            limit = 20;
        }
        return ResponseEntity.ok(bookService.getPopularBooks(limit));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<
            BookAvailabilityDto
            > getBookAvailability(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookAvailability(id));
    }


}
