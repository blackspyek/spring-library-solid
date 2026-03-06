package org.pollub.catalog.service;

import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.pollub.catalog.flyweight.SortFlyweightFactory;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.pollub.catalog.model.SearchCriteria;
import org.pollub.catalog.model.dto.BookAvailabilityDto;
import org.pollub.catalog.model.dto.BookCreateDto;
import org.pollub.catalog.repository.IBranchInventoryRepository;
import org.pollub.catalog.repository.IBookRepository;
import org.pollub.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService implements IBookService {
    private final IBookRepository bookRepository;
    private final IBranchInventoryRepository inventoryRepository;


    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> getBooksPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return bookRepository.findAll(pageable);
    }

    @Override
    public Book createBook(BookCreateDto dto) {
        Book book = new Book();
        mapBookFromDto(book, dto);
        return saveOrThrow(book);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id " + id + " not found."));
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Override
    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findByGenre(String genre) {
        return bookRepository.findByGenre(genre);
    }

    @Override
    public Book updateBook(Long id, BookCreateDto dto) {
        var book = findById(id);
        mapBookFromDto(book, dto);
        return saveOrThrow(book);
    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public List<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    private void mapBookFromDto(Book book, BookCreateDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setPageCount(dto.getPageCount());
        book.setPaperType(dto.getPaperType());
        book.setPublisher(dto.getPublisher());
        book.setShelfNumber(dto.getShelfNumber());
        book.setGenre(dto.getGenre());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
    }
    
    private Book saveOrThrow(Book book) {
        Book savedBook = bookRepository.save(book);
        return Optional.of(savedBook)
                .orElseThrow(() -> new PersistenceException("Failed to save the book."));
    }

    @Override
    public Page<Book> searchBooks(SearchCriteria criteria) {

        //Lab1 - Simple Factory 2 Start
        Sort sortSpec = SortFlyweightFactory.getSort(criteria.getSort());
        //Lab1 End Simple Factory 2

        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sortSpec);

        String queryParam = (criteria.getQuery() != null && !criteria.getQuery().isBlank()) ? criteria.getQuery() : null;
        String publisherParam = (criteria.getPublisher() != null && !criteria.getPublisher().isBlank()) ? criteria.getPublisher() : null;
        String genresParam = (criteria.getGenres() != null && !criteria.getGenres().isEmpty()) ? criteria.getGenres() : null;

        return bookRepository.searchBooksWithoutStatus(queryParam, publisherParam, genresParam, pageable);
    }

    @Override
    public List<String> getTopGenres() {
        return bookRepository.findTop4Genres();
    }

    @Override
    public List<String> getOtherGenres() {
        return bookRepository.findOtherGenres();
    }

    @Override
    public List<String> getAllPublishers() {
        return bookRepository.findAllPublishers();
    }

    @Override
    public List<String> getAllStatuses() {
        //Lab1 - Flyweight 3 Method Start
        return CopyStatus.ALL_NAMES;
        //Lab1 - Flyweight 3 Method End
    }

    @Override
    public List<Book> getRecentBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return bookRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public List<Book> getPopularBooks(int limit) {
        // Since we are in microservices, we cannot access rental history directly here 
        // without an inter-service call or data replication.
        // For now, fallback to recent books.
        return getRecentBooks(limit);
    }

    @Override
    public BookAvailabilityDto getBookAvailability(Long id) {
        Book book = findById(id);
        
        // Get inventory info for this book
        List<BranchInventory> inventories = inventoryRepository.findByItemId(id);
        
        // Find any rented copy to show days until due
        Integer daysUntilDue = null;
        BranchInventory rentedCopy = inventories.stream()
                .filter(inv -> inv.getStatus() == CopyStatus.RENTED && inv.getDueDate() != null)
                .findFirst()
                .orElse(null);
        
        if (rentedCopy != null && rentedCopy.getDueDate() != null) {
            long days = ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(), 
                    rentedCopy.getDueDate().toLocalDate()
            );
            daysUntilDue = (int) Math.max(0, days);
        }
        
        // Get available branches
        Set<Long> availableBranches = inventories.stream()
                .filter(inv -> inv.getStatus() == CopyStatus.AVAILABLE)
                .map(BranchInventory::getBranchId)
                .collect(Collectors.toSet());
        
        // Compute overall status
        String overallStatus = availableBranches.isEmpty() ? "UNAVAILABLE" : "AVAILABLE";

        return BookAvailabilityDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .status(overallStatus)
                .imageUrl(book.getImageUrl())
                .daysUntilDue(daysUntilDue)
                .availableAtBranches(availableBranches)
                .build();
    }
}