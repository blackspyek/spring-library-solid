package org.pollub.library.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pollub.library.config.TestSecurityConfig;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.dto.BookCreateDto;
import org.pollub.library.item.repository.IBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@Import(TestSecurityConfig.class)
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book testBook;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle("Existing Book");
        book.setAuthor("Author");
        book.setIsbn("1234567890123");
        book.setPageCount(200);
        book.setPublisher("Publisher");
        book.setShelfNumber(1);
        book.setGenre("Non-fiction");
        book.setPaperType("Hardcover");
        book.setDescription("Existing Book Description");
        return bookRepository.save(book);
    }

    @Test
    void givenBookCreateDto_whenCreateBook_thenStatus200() throws Exception {
        BookCreateDto bookCreateDto = getCreateDto();

        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Book")))
                .andExpect(jsonPath("$.author", is("Test Author")))
                .andExpect(jsonPath("$.pageCount", is(200)))
                .andExpect(jsonPath("$.paperType", is("Paperback")))
                .andExpect(jsonPath("$.publisher", is("Test Publisher")))
                .andExpect(jsonPath("$.shelfNumber", is(1)))
                .andExpect(jsonPath("$.genre", is("Fiction")))
                .andExpect(jsonPath("$.isbn", is("9788381438728")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    private static BookCreateDto getCreateDto() {
        BookCreateDto bookCreateDto = new BookCreateDto();
        bookCreateDto.setTitle("Test Book");
        bookCreateDto.setAuthor("Test Author");
        bookCreateDto.setPageCount(200);
        bookCreateDto.setPaperType("Paperback");
        bookCreateDto.setPublisher("Test Publisher");
        bookCreateDto.setShelfNumber(1);
        bookCreateDto.setGenre("Fiction");
        bookCreateDto.setIsbn("9788381438728");
        bookCreateDto.setDescription("Test Description");
        return bookCreateDto;
    }

    @Test
    void givenBookId_whenGetBook_thenStatus200() throws Exception {
        testBook = createTestBook();
        mockMvc.perform(get("/api/book/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Existing Book")))
                .andExpect(jsonPath("$.author", is("Author")))
                .andExpect(jsonPath("$.isbn", is("1234567890123")))
                .andExpect(jsonPath("$.pageCount", is(200)))
                .andExpect(jsonPath("$.publisher", is("Publisher")))
                .andExpect(jsonPath("$.genre", is("Non-fiction")))
                .andExpect(jsonPath("$.paperType", is("Hardcover")))
                .andExpect(jsonPath("$.description", is("Existing Book Description")))
                .andExpect(jsonPath("$.shelfNumber", is(1)));
    }

    @Test
    void givenBookId_whenUpdateBook_thenStatus200() throws Exception {
        testBook = createTestBook();
        BookCreateDto updatedBookDto = getBookCreateDto();

        mockMvc.perform(put("/api/book/{id}", testBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.author", is("Updated Author")))
                .andExpect(jsonPath("$.isbn", is("9788383612645")))
                .andExpect(jsonPath("$.pageCount", is(300)))
                .andExpect(jsonPath("$.publisher", is("Updated Publisher")))
                .andExpect(jsonPath("$.genre", is("Science Fiction")))
                .andExpect(jsonPath("$.paperType", is("Paperback")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.shelfNumber", is(2)));
    }

    private static BookCreateDto getBookCreateDto() {
        BookCreateDto updatedBookDto = new BookCreateDto();
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setAuthor("Updated Author");
        updatedBookDto.setDescription("Updated description");
        updatedBookDto.setIsbn("9788383612645");
        updatedBookDto.setPageCount(300);
        updatedBookDto.setPublisher("Updated Publisher");
        updatedBookDto.setShelfNumber(2);
        updatedBookDto.setGenre("Science Fiction");
        updatedBookDto.setPaperType("Paperback");
        return updatedBookDto;
    }

    @Test
    void givenBookId_whenDeleteBook_thenStatus200() throws Exception {
        testBook = createTestBook();
        mockMvc.perform(delete("/api/book/{id}", testBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Book deleted")));

        mockMvc.perform(get("/api/book/{id}", testBook.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenAuthor_whenGetBooksByAuthor_thenStatus200() throws Exception {
        testBook = createTestBook();
        mockMvc.perform(get("/api/book/author/{author}", "Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Existing Book")))
                .andExpect(jsonPath("$[0].author", is("Author")));
    }
}
