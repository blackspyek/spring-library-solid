package org.pollub.library.item.service;

import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;

import java.util.List;
import java.util.Optional;

public interface IMovieService {
    MovieDisc createMovie(MovieCreateDto dto);
    MovieDisc findById(Long id);
    List<MovieDisc> findByDirector(String director);
    List<MovieDisc> findByTitle(String title);
    List<MovieDisc> findByGenre(String genre);
    List<MovieDisc> findByFileFormat(String fileFormat);
    List<MovieDisc> findByResolution(String resolution);
    List<MovieDisc> findByDurationBetween(Integer minDuration, Integer maxDuration);
    Optional<MovieDisc> findByTitleAndDirector(String title, String director);
    MovieDisc updateMovie(Long id, MovieCreateDto dto);
    void deleteMovie(Long id);
}
