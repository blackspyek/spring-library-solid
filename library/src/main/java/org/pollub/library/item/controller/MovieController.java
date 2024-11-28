package org.pollub.library.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pollub.library.item.model.MovieDisc;
import org.pollub.library.item.model.dto.MovieCreateDto;
import org.pollub.library.item.service.IMovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movie")
@RequiredArgsConstructor
public class MovieController {
    private final IMovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<MovieDisc> createMovie(@Valid @RequestBody MovieCreateDto dto) {
        return ResponseEntity.ok(movieService.createMovie(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MovieDisc> getMovie(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.findById(id));
    }

    @GetMapping("/director/{director}")
    public ResponseEntity<List<MovieDisc>> getMoviesByDirector(@PathVariable String director) {
        return ResponseEntity.ok(movieService.findByDirector(director));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<List<MovieDisc>> getMoviesByTitle(@PathVariable String title) {
        return ResponseEntity.ok(movieService.findByTitle(title));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieDisc>> getMoviesByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.findByGenre(genre));
    }

    @GetMapping("/fileFormat/{fileFormat}")
    public ResponseEntity<List<MovieDisc>> getMoviesByFileFormat(@PathVariable String fileFormat) {
        return ResponseEntity.ok(movieService.findByFileFormat(fileFormat));
    }

    @GetMapping("/resolution/{resolution}")
    public ResponseEntity<List<MovieDisc>> getMoviesByResolution(@PathVariable String resolution) {
        return ResponseEntity.ok(movieService.findByResolution(resolution));
    }

    @GetMapping("/duration")
    public ResponseEntity<List<MovieDisc>> getMoviesByDurationBetween(
            @RequestParam Integer minDuration, @RequestParam Integer maxDuration) {
        return ResponseEntity.ok(movieService.findByDurationBetween(minDuration, maxDuration));
    }

    @GetMapping("/titleAndDirector")
    public ResponseEntity<MovieDisc> getMovieByTitleAndDirector(
            @RequestParam String title, @RequestParam String director) {
        MovieDisc movie = movieService.findByTitleAndDirector(title, director);
        return ResponseEntity.ok(movie);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<MovieDisc> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieCreateDto dto
    ) {
        return ResponseEntity.ok(movieService.updateMovie(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Movie deleted");
    }
}
