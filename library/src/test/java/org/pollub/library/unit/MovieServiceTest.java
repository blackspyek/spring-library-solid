package org.pollub.library.unit;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;
import org.pollub.library.item.repository.IMovieRepository;
import org.pollub.library.item.service.MovieService;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class MovieServiceTest {

    @Mock
    private IMovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private MovieCreateDto movieCreateDto;

    @BeforeEach
    void setUp() {
        createMovieDto();
    }

    private void createMovieDto() {
        movieCreateDto = new MovieCreateDto();
        movieCreateDto.setTitle("Test Movie");
        movieCreateDto.setDirector("Test Director");
        movieCreateDto.setResolution("1080p");
        movieCreateDto.setDuration(120);
        movieCreateDto.setFileFormat("MP4");
        movieCreateDto.setGenre("Action");
        movieCreateDto.setShelfNumber(1);
        movieCreateDto.setDigitalRights("Test DigitalRights");
    }

    @Test
    @DisplayName("Ensure correct movie creation with given MovieCreateDto")
    void givenMovieCreateDto_whenCreateMovie_thenReturnMovie() {
        MovieDisc movie = new MovieDisc();
        when(movieRepository.save(any(MovieDisc.class))).thenReturn(movie);

        MovieDisc createdMovie = movieService.createMovie(movieCreateDto);

        ArgumentCaptor<MovieDisc> movieCaptor = ArgumentCaptor.forClass(MovieDisc.class);
        verify(movieRepository).save(movieCaptor.capture());
        MovieDisc savedMovie = movieCaptor.getValue();

        assertNotNull(createdMovie);
        assertEquals("Test Movie", savedMovie.getTitle());
        assertEquals("Test Director", savedMovie.getDirector());
        assertEquals("1080p", savedMovie.getResolution());
        assertEquals(120, savedMovie.getDuration());
        assertEquals("MP4", savedMovie.getFileFormat());
        assertEquals("Action", savedMovie.getGenre());
        assertEquals(1, savedMovie.getShelfNumber());
        assertEquals("Test DigitalRights", savedMovie.getDigitalRights());
    }

    @Test
    @DisplayName("Throw EntityNotFoundException for invalid movie ID")
    void givenInvalidMovieId_whenFindById_thenThrowEntityNotFoundException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> movieService.findById(1L));
    }

    @Test
    @DisplayName("Ensure correct update with given MovieCreateDto")
    void givenMovieCreateDto_whenUpdateMovie_thenReturnUpdatedMovie() {
        MovieDisc movieToUpdate = getMovieDisc();

        when(movieRepository.findById(1L)).thenReturn(Optional.of(movieToUpdate));
        when(movieRepository.save(any(MovieDisc.class))).thenReturn(movieToUpdate);

        movieCreateDto.setTitle("Updated Test Movie");

        MovieDisc updatedMovie = movieService.updateMovie(1L, movieCreateDto);

        ArgumentCaptor<MovieDisc> updatedMovieCaptor = ArgumentCaptor.forClass(MovieDisc.class);
        verify(movieRepository).save(updatedMovieCaptor.capture());
        MovieDisc updatedSavedMovie = updatedMovieCaptor.getValue();

        assertNotNull(updatedMovie);
        assertEquals("Updated Test Movie", updatedSavedMovie.getTitle());
        assertEquals("Test Director", updatedSavedMovie.getDirector());
        assertEquals("1080p", updatedSavedMovie.getResolution());
        assertEquals(120, updatedSavedMovie.getDuration());
        assertEquals("MP4", updatedSavedMovie.getFileFormat());
        assertEquals("Action", updatedSavedMovie.getGenre());
        assertEquals(1, updatedSavedMovie.getShelfNumber());
        assertEquals("Test DigitalRights", updatedSavedMovie.getDigitalRights());
    }

    private static MovieDisc getMovieDisc() {
        MovieDisc movieToUpdate = new MovieDisc();
        movieToUpdate.setId(1L);
        movieToUpdate.setTitle("Test Movie");
        movieToUpdate.setDirector("Test Director");
        movieToUpdate.setResolution("1080p");
        movieToUpdate.setDuration(120);
        movieToUpdate.setFileFormat("MP4");
        movieToUpdate.setGenre("Action");
        movieToUpdate.setShelfNumber(1);
        movieToUpdate.setDigitalRights("Test DigitalRights");
        return movieToUpdate;
    }


    @Test
    @DisplayName("Throw EntityNotFoundException for non-existing movie in deleteMovie")
    void givenNonExistingMovie_whenDeleteMovie_thenThrowEntityNotFoundException() {
        when(movieRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> movieService.deleteMovie(1L));
    }

    @Test
    @DisplayName("Successfully delete existing movie with given ID")
    void givenExistingMovie_whenDeleteMovie_thenDeleteMovie() {
        when(movieRepository.existsById(1L)).thenReturn(true);

        movieService.deleteMovie(1L);

        verify(movieRepository).deleteById(1L);
    }


}
