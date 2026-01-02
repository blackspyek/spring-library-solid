package org.pollub.catalog.repository;

import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBookRepository extends ILibraryItemRepository<Book> {

    List<Book> findAll();
    Page<Book> findAll(Pageable pageable);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByTitle(String title);
    List<Book> findByGenre(String genre);
    List<Book> findByIsbn(String isbn);

    // Recent books - ordered by creation date descending
    List<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find books by IDs preserving order
    @Query("SELECT b FROM Book b WHERE b.id IN :ids")
    List<Book> findByIdIn(@Param("ids") List<Long> ids);

    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', COALESCE(:query, ''), '%')) " +
            "   OR LOWER(b.author) LIKE LOWER(CONCAT('%', COALESCE(:query, ''), '%')) " +
            "   OR :query IS NULL) " +
            "AND (LOWER(b.publisher) = LOWER(COALESCE(:publisher, b.publisher))) " +
            "AND (b.genre IN :genres OR :genres IS NULL)")
    Page<Book> searchBooksWithoutStatus(
            @Param("query") String query,
            @Param("publisher") String publisher,
            @Param("genres") String genres,
            Pageable pageable
    );
    //trzeba takie dziwne, bo jest jakis bug w spring data jpa z bytea i nie da sie tego inaczej obejsc

    @Query(
            value = "SELECT b.genre " +
                    "FROM books b " +
                    "GROUP BY b.genre " +
                    "ORDER BY COUNT(b.id) DESC " +
                    "LIMIT 4",
            nativeQuery = true
    )
    List<String> findTop4Genres();

    @Query(
            value = "SELECT b.genre " +
                    "FROM books b " +
                    "GROUP BY b.genre " +
                    "ORDER BY COUNT(b.id) DESC, b.genre ASC " +
                    "OFFSET 4",
            nativeQuery = true
    )
    List<String> findOtherGenres();

    @Query("SELECT DISTINCT b.publisher FROM Book b ORDER BY b.publisher ASC")
    List<String> findAllPublishers();

}