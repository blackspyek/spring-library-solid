package org.pollub.library.item.service;

import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;

import java.util.List;

public interface IMovieService {
    MovieDisc createMovie(MovieCreateDto dto);
    MovieDisc findById(Long id);
    List<MovieDisc> findByDirector(String director);
    List<MovieDisc> findByGenre(String genre);
    MovieDisc updateMovie(Long id, MovieCreateDto dto);
    void deleteMovie(Long id);
}
