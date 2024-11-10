package org.pollub.library.item.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;
import org.pollub.library.item.repository.IMovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService implements IMovieService{
    private final IMovieRepository movieRepository;

    @Override
    public MovieDisc createMovie(MovieCreateDto dto) {
        MovieDisc movie = new MovieDisc();
        mapMovieFromDto(movie, dto);
        return saveOrThrow(movie);
    }

    @Override
    public MovieDisc findById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movie with id " + id + " not found."));
    }

    @Override
    public List<MovieDisc> findByDirector(String director) {
        return movieRepository.findByDirectorContainingIgnoreCase(director);
    }

    @Override
    public List<MovieDisc> findByGenre(String genre) {
        return movieRepository.findByGenre(genre);
    }

    @Override
    public MovieDisc updateMovie(Long id, MovieCreateDto dto) {
        var movie = findById(id);
        mapMovieFromDto(movie, dto);
        return saveOrThrow(movie);
    }

    @Override
    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new EntityNotFoundException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    private void mapMovieFromDto(MovieDisc movie, MovieCreateDto dto) {
        movie.setTitle(dto.getTitle());
        movie.setDirector(dto.getDirector());
        movie.setResolution(dto.getResolution());
        movie.setDuration(dto.getDuration());
        movie.setFileFormat(dto.getFileFormat());
        movie.setGenre(dto.getGenre());
        movie.setShelfNumber(dto.getShelfNumber());
        movie.setDigitalRights(dto.getDigitalRights());
    }
    private MovieDisc saveOrThrow(MovieDisc movie) {
        MovieDisc savedMovie = movieRepository.save(movie);
        return Optional.of(savedMovie)
                .orElseThrow(() -> new PersistenceException("Failed to save the movie."));
    }
}