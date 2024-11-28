package org.pollub.library.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pollub.library.config.TestSecurityConfig;
import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;
import org.pollub.library.item.repository.IMovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("ALL")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
@Import(TestSecurityConfig.class)
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MovieDisc testMovie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    private MovieDisc createTestMovie() {
        MovieDisc movie = new MovieDisc();
        movie.setTitle("Existing Movie");
        movie.setDirector("Director");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setFileFormat("Blu-ray");
        movie.setResolution("1080p");
        movie.setDescription("Movie Description");
        movie.setShelfNumber(1);
        movie.setDigitalRights("Test DigitalRights");
        return movieRepository.save(movie);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void givenMovieCreateDto_whenCreateMovie_thenStatus200() throws Exception {
        MovieCreateDto movieCreateDto = getCreateDto();

        mockMvc.perform(post("/api/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Movie")))
                .andExpect(jsonPath("$.director", is("Test Director")))
                .andExpect(jsonPath("$.genre", is("Comedy")))
                .andExpect(jsonPath("$.duration", is(90)))
                .andExpect(jsonPath("$.fileFormat", is("DVD")))
                .andExpect(jsonPath("$.resolution", is("720p")))
                .andExpect(jsonPath("$.shelfNumber", is(1)))
                .andExpect(jsonPath("$.digitalRights", is("Test DigitalRights")));
    }

    private static MovieCreateDto getCreateDto() {
        MovieCreateDto movieCreateDto = new MovieCreateDto();
        movieCreateDto.setTitle("Test Movie");
        movieCreateDto.setDirector("Test Director");
        movieCreateDto.setGenre("Comedy");
        movieCreateDto.setDuration(90);
        movieCreateDto.setFileFormat("DVD");
        movieCreateDto.setResolution("720p");
        movieCreateDto.setDescription("Test Description");
        movieCreateDto.setShelfNumber(1);
        movieCreateDto.setDigitalRights("Test DigitalRights");
        return movieCreateDto;
    }

    @Test
    void givenMovieId_whenGetMovie_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(get("/api/movie/{id}", testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Existing Movie")))
                .andExpect(jsonPath("$.director", is("Director")))
                .andExpect(jsonPath("$.genre", is("Action")))
                .andExpect(jsonPath("$.duration", is(120)))
                .andExpect(jsonPath("$.fileFormat", is("Blu-ray")))
                .andExpect(jsonPath("$.resolution", is("1080p")))
                .andExpect(jsonPath("$.description", is("Movie Description")))
                .andExpect(jsonPath("$.shelfNumber", is(1)))
                .andExpect(jsonPath("$.digitalRights", is("Test DigitalRights")));
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void givenMovieId_whenUpdateMovie_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        MovieCreateDto updatedMovieDto = getMovieCreateDto();

        mockMvc.perform(put("/api/movie/{id}", testMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovieDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Movie Title")))
                .andExpect(jsonPath("$.director", is("Updated Director")))
                .andExpect(jsonPath("$.genre", is("Drama")))
                .andExpect(jsonPath("$.duration", is(150)))
                .andExpect(jsonPath("$.fileFormat", is("Blu-ray")))
                .andExpect(jsonPath("$.resolution", is("4K")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.shelfNumber", is(1)))
                .andExpect(jsonPath("$.digitalRights", is("Test DigitalRights")));
    }

    private static MovieCreateDto getMovieCreateDto() {
        MovieCreateDto updatedMovieDto = new MovieCreateDto();
        updatedMovieDto.setTitle("Updated Movie Title");
        updatedMovieDto.setDirector("Updated Director");
        updatedMovieDto.setGenre("Drama");
        updatedMovieDto.setDuration(150);
        updatedMovieDto.setFileFormat("Blu-ray");
        updatedMovieDto.setResolution("4K");
        updatedMovieDto.setDescription("Updated description");
        updatedMovieDto.setShelfNumber(1);
        updatedMovieDto.setDigitalRights("Test DigitalRights");
        return updatedMovieDto;
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void givenMovieId_whenDeleteMovie_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(delete("/api/movie/{id}", testMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Movie deleted")));

        mockMvc.perform(get("/api/movie/{id}", testMovie.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenDirector_whenGetMoviesByDirector_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(get("/api/movie/director/{director}", "Director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Existing Movie")))
                .andExpect(jsonPath("$[0].director", is("Director")))
                .andExpect(jsonPath("$[0].shelfNumber", is(1)))
                .andExpect(jsonPath("$[0].digitalRights", is("Test DigitalRights")));
    }

    @Test
    void givenGenre_whenGetMoviesByGenre_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(get("/api/movie/genre/{genre}", "Action"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Existing Movie")))
                .andExpect(jsonPath("$[0].genre", is("Action")))
                .andExpect(jsonPath("$[0].shelfNumber", is(1)))
                .andExpect(jsonPath("$[0].digitalRights", is("Test DigitalRights")));
    }

    @Test
    void givenFileFormat_whenGetMoviesByFileFormat_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(get("/api/movie/fileFormat/{fileFormat}", "Blu-ray"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Existing Movie")))
                .andExpect(jsonPath("$[0].fileFormat", is("Blu-ray")))
                .andExpect(jsonPath("$[0].shelfNumber", is(1)))
                .andExpect(jsonPath("$[0].digitalRights", is("Test DigitalRights")));
    }

    @Test
    void givenDurationRange_whenGetMoviesByDuration_thenStatus200() throws Exception {
        testMovie = createTestMovie();
        mockMvc.perform(get("/api/movie/duration")
                        .param("minDuration", "100")
                        .param("maxDuration", "130"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Existing Movie")))
                .andExpect(jsonPath("$[0].duration", is(120)))
                .andExpect(jsonPath("$[0].shelfNumber", is(1)))
                .andExpect(jsonPath("$[0].digitalRights", is("Test DigitalRights")));
    }
}
