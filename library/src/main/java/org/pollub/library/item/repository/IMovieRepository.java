package org.pollub.library.item.repository;

import org.pollub.library.item.model.MovieDisc;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMovieRepository extends ILibraryItemRepository<MovieDisc> {
    List<MovieDisc> findByDirectorContainingIgnoreCase(String director);
    List<MovieDisc> findByGenre(String genre);
    List<MovieDisc> findByFileFormat(String fileFormat);
    List<MovieDisc> findByResolution(String resolution);
    List<MovieDisc> findByDurationBetween(Integer minDuration, Integer maxDuration);
    Optional<MovieDisc> findByTitleAndAndDirector(String title, String director);
}
