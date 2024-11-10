package org.pollub.library.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.pollub.library.item.service.IBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {
    private final IBookService bookService;

    @PostMapping
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

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(bookService.findByGenre(genre));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.findByIsbn(isbn));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookCreateDto dto
    ) {
        return ResponseEntity.ok(bookService.updateBook(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }
}
