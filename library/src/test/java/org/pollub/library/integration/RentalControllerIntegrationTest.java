package org.pollub.library.integration;

import org.junit.jupiter.api.Test;
import org.pollub.library.config.TestSecurityConfig;
import org.pollub.library.item.model.Book;
import org.pollub.library.item.model.ItemStatus;
import org.pollub.library.item.repository.IBookRepository;
import org.pollub.library.user.model.User;
import org.pollub.library.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@Import(TestSecurityConfig.class)
class RentalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IBookRepository bookRepository;

    @Autowired
    private IUserRepository userRepository;

    @Test
    void givenAvailableItems_whenGetAvailableItems_thenStatus200() throws Exception {
        createTestBook(1L, "Book 1", "A description", ItemStatus.AVAILABLE, "1234567891111", "Author 1", 100, "Paperback", "Publisher 1", 1, "Fiction", null, null, null);

        mockMvc.perform(get("/api/rentals/available")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Book 1")))
                .andExpect(jsonPath("$[0].description", is("A description")))
                .andExpect(jsonPath("$[0].status", is("AVAILABLE")));
    }

    @Test
    void givenBook_whenRentBook_thenStatus200() throws Exception {
        createTestBook(1L, "Book to Rent", "Description", ItemStatus.AVAILABLE, "1234567891111", "Author", 150, "Paperback", "Publisher", 2, "Non-fiction", null, null, null);

        createTestUser(1L, "user@example.com", "password", "user123");

        String rentDtoJson = """
        {
            "userId": 1,
            "libraryItemId": 1
        }
        """;

        mockMvc.perform(post("/api/rentals/rent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Book to Rent")))
                .andExpect(jsonPath("$.status", is("RENTED")));
    }

    @Test
    void givenRentedItems_whenGetUserRentals_thenStatus200() throws Exception {
        createTestUser(1L, "user@example.com", "password", "user123");
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        createTestBook(1L, "Book 1", "A description", ItemStatus.RENTED, "1234567891111", "Author 1", 100, "Paperback", "Publisher 1", 1, "Fiction", user, LocalDateTime.now(), LocalDateTime.now().plusDays(14));

        mockMvc.perform(get("/api/rentals/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("RENTED")));
    }

    @Test
    void givenRentedItem_whenReturnItem_thenStatus200() throws Exception {
        createTestUser(1L, "user@example.com", "password", "user123");
        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));
        createTestBook(1L, "Book to Return", "Description", ItemStatus.RENTED, "1234567891111", "Author", 150, "Paperback", "Publisher", 2, "Non-fiction", user, LocalDateTime.now(), LocalDateTime.now().plusDays(14));

        mockMvc.perform(post("/api/rentals/return/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("AVAILABLE")));
    }


    private void createTestBook(Long id, String title, String description, ItemStatus status, String isbn, String author, int pageCount, String paperType, String publisher, int shelfNumber, String genre, User rentedByUser, LocalDateTime rentedAt, LocalDateTime dueDate) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setDescription(description);
        book.setStatus(status);
        book.setIsbn(isbn);
        book.setAuthor(author);
        book.setPageCount(pageCount);
        book.setPaperType(paperType);
        book.setPublisher(publisher);
        book.setShelfNumber(shelfNumber);
        book.setGenre(genre);

        if (rentedByUser != null) {
            book.setRentedByUser(rentedByUser);
            book.setRentedAt(rentedAt);
            book.setDueDate(dueDate);
        }

        bookRepository.save(book);
    }

    private void createTestUser(Long id, String email, String password, String username) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        userRepository.save(user);
    }
}
