package org.pollub.library.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.pollub.library.item.repository.IBookRepository;
import org.pollub.library.item.service.BookService;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceTest {

    @Mock
    private IBookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookCreateDto bookCreateDto;
    private Book book;

    @BeforeEach
    void setUp() {
        createBookDto();
    }

    private void createBookDto() {
        bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle("Test Book");
        bookCreateDto.setAuthor("Test Author");
        bookCreateDto.setPageCount(200);
        bookCreateDto.setPaperType("Paperback");
        bookCreateDto.setPublisher("Test Publisher");
        bookCreateDto.setShelfNumber(1);
        bookCreateDto.setGenre("Fiction");
        bookCreateDto.setIsbn("123-456-789");
        bookCreateDto.setDescription("Test Description");
    }

    @Test
    @DisplayName("Ensure correct book creation with given BookCreateDto")
    void givenBookCreateDto_whenCreateBook_thenReturnBook() {
        book = new Book();
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(bookCreateDto);

        ArgumentCaptor<Book> bookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(bookCaptor.capture());
        Book savedBook = bookCaptor.getValue();

        assertNotNull(createdBook);
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("Test Author", savedBook.getAuthor());
        assertEquals(200, savedBook.getPageCount());
        assertEquals("Paperback", savedBook.getPaperType());
        assertEquals("Test Publisher", savedBook.getPublisher());
        assertEquals(1, savedBook.getShelfNumber());
        assertEquals("Fiction", savedBook.getGenre());
        assertEquals("123-456-789", savedBook.getIsbn());
        assertEquals("Test Description", savedBook.getDescription());
    }

    @Test
    @DisplayName("Successfully find book by valid ID")
    void givenValidBookId_whenFindById_thenReturnBook() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book foundBook = bookService.findById(1L);

        verify(bookRepository).findById(1L);
        assertNotNull(foundBook);
        assertEquals(1L, foundBook.getId());
        assertEquals("Test Book", foundBook.getTitle());
    }

    @Test
    @DisplayName("Ensure correct update with given BookCreateDto")
    void givenBookCreateDto_whenUpdateBook_thenReturnUpdatedBook() {
        Book bookToUpdate = new Book();
        bookToUpdate.setId(1L);
        bookToUpdate.setTitle("Test Book");
        bookToUpdate.setAuthor("Test Author");
        bookToUpdate.setPageCount(200);
        bookToUpdate.setPaperType("Paperback");
        bookToUpdate.setPublisher("Test Publisher");
        bookToUpdate.setShelfNumber(1);
        bookToUpdate.setGenre("Fiction");
        bookToUpdate.setIsbn("123-456-789");
        bookToUpdate.setDescription("Test Description");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToUpdate));
        when(bookRepository.save(any(Book.class))).thenReturn(bookToUpdate);

        bookCreateDto.setTitle("Updated Test Book");

        Book updatedBook = bookService.updateBook(1L, bookCreateDto);

        ArgumentCaptor<Book> updatedBookCaptor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(updatedBookCaptor.capture());
        Book updatedSavedBook = updatedBookCaptor.getValue();

        assertNotNull(updatedBook);
        assertEquals("Updated Test Book", updatedSavedBook.getTitle());
        assertEquals("Test Author", updatedSavedBook.getAuthor());
        assertEquals(200, updatedSavedBook.getPageCount());
        assertEquals("Paperback", updatedSavedBook.getPaperType());
        assertEquals("Test Publisher", updatedSavedBook.getPublisher());
        assertEquals(1, updatedSavedBook.getShelfNumber());
        assertEquals("Fiction", updatedSavedBook.getGenre());
        assertEquals("123-456-789", updatedSavedBook.getIsbn());
        assertEquals("Test Description", updatedSavedBook.getDescription());
    }


    @Test
    @DisplayName("Throw EntityNotFoundException when deleting non-existing book")
    void givenNonExistingBook_whenDeleteBook_thenThrowEntityNotFoundException() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    @DisplayName("Successfully delete existing book with given ID")
    void givenExistingBook_whenDeleteBook_thenDeleteBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.deleteBook(1L);

        verify(bookRepository).deleteById(1L);
    }


}