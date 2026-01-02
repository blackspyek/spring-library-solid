package org.pollub.catalog.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pollub.catalog.model.Book;
import org.pollub.catalog.model.BranchInventory;
import org.pollub.catalog.model.CopyStatus;
import org.pollub.catalog.model.ItemType;
import org.pollub.catalog.repository.IBranchInventoryRepository;
import org.pollub.catalog.repository.IBookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IBookRepository bookRepository;
    private final IBranchInventoryRepository branchInventoryRepository;

    @Override
    public void run(String... args) {
        if (bookRepository.count() == 0) {
            log.info("No items found. Populating catalog with legacy book data...");

            List<Book> books = Arrays.asList(
                createBook("Fondane, Maritain", "Benjamin Fondane", "Correspondence", "284272013X", 216, "A4", "Paris-Méditerranée", 1, "Includes bibliographical references (p. 211-212) and index.", "https://covers.openlibrary.org/b/id/2173986-L.jpg", 2022, true),
                createBook("Treasures of the Aquarians", "Davis, Richard", "Humor", "0140080368", 93, "A4", "Penguin", 1, null, "https://covers.openlibrary.org/b/id/5728544-L.jpg", 2022, true),
                createBook("A man named Thoreau", "Robert Burleigh", "Juvenile literature", "0689311222", 31, "A4", "Atheneum", 1, "Presents the life and ideas of the renowned nineteenth-century American author.", "https://covers.openlibrary.org/b/id/4212465-L.jpg", 2010, true),
                createBook("To tell a free story", "William L. Andrews", "Biography", "0252012224", 353, "A4", "University of Illinois Press", 1, "Discusses the writings of Richard Allen...", "https://covers.openlibrary.org/b/id/4213036-L.jpg", 2017, true),
                createBook("Get that pest!", "Erin Douglas", "Fiction", "0152025480", -1, "A4", "Green Light Readers", 1, "When a farmer and his wife discover...", "https://covers.openlibrary.org/b/id/1120285-L.jpg", 2015, true),
                createBook("The challenge of aging", "Margaret Ellen Monroe", "Bibliography", "0872873870", 209, "A4", "Libraries Unlimited", 1, "Includes indexes.", "https://covers.openlibrary.org/b/id/4797808-L.jpg", 2025, true),
                createBook("Teenage stress", "Susan Cohen", "Juvenile literature", "0871314231", 175, "A4", "M. Evans", 1, "Discusses stress and its causes...", "https://covers.openlibrary.org/b/id/1618587-L.jpg", 2013, true),
                createBook("Martin Eden", "Jack London", "Fiction", "0140390367", 482, "A4", "Penguin Books", 1, "Bibliography: p. [23]-26.", "https://covers.openlibrary.org/b/id/4228639-L.jpg", 2024, false),
                createBook("Dog tales", "Jennifer Rae", "Fiction", "1582460116", 28, "A4", "Tricycle Press", 1, "Presents well-known fairy tales...", "https://covers.openlibrary.org/b/id/3695620-L.jpg", 2010, false),
                createBook("The USA comprehensive public camping guide", "Jeanne Bowerman", "Directories", "0971694761", 11, "A4", "Jeanne Bowerman", 1, "Presents a list of public camping areas...", "https://covers.openlibrary.org/b/id/1710427-L.jpg", 2010, false),
                createBook("The Rose and the Warrior", "Karyn Monk", "Fiction", "0553577611", 324, "A4", "Bantam Books", 1, null, "https://covers.openlibrary.org/b/id/372718-L.jpg", 2021, false),
                createBook("Silverbridge", "Joan Wolf", "Fiction", "0446610429", 386, "A4", "Warner", 1, null, "https://covers.openlibrary.org/b/id/287225-L.jpg", 2016, false),
                createBook("Remnants", "Katherine Applegate", "Juvenile fiction", "9780590884948", 138, "A4", "Scholastic Inc.", 1, null, "https://covers.openlibrary.org/b/id/12656521-L.jpg", 2025, true),
                createBook("Innocent betrayal", "Mary Campisi", "Fiction", "0821766023", 351, "A4", "Kensington Pub. Corp.", 1, null, "https://covers.openlibrary.org/b/id/620493-L.jpg", 2023, false),
                createBook("Gunfight at Santa Angela", "Tom Calhoun", "Fiction", "0515134694", 184, "A4", "Jove Books", 1, "\"Jove Western\"--Spine.", "https://covers.openlibrary.org/b/id/1261741-L.jpg", 2016, false),
                createBook("A Kiss at Midnight", "Lynn Collum", "Fiction", "0821772910", 219, "A4", "Zebra Boks", 1, null, "https://covers.openlibrary.org/b/id/620904-L.jpg", 2024, false),
                createBook("Wyoming wolf pack", "Jon Sharpe", "Fiction", "0451208609", 168, "A4", "Signet", 1, null, "https://covers.openlibrary.org/b/id/1236739-L.jpg", 2023, false),
                createBook("Settlement houses", "Friedman, Michael", "Juvenile literature", "1404201947", -1, "A4", "Rosen Pub. Group", 1, "Includes bibliographical references...", "https://covers.openlibrary.org/b/id/1746589-L.jpg", 2014, false),
                createBook("We're off to Harvard Square --", "Sage Stossel", "Fiction", "188983386X", -1, "A4", "Commonwealth Eds.", 1, "Detailed illustrations...", "https://covers.openlibrary.org/b/id/2092616-L.jpg", 2021, false),
                createBook("Hmong and American", "Sue Murphy Mote", "Biography", "078641832X", 306, "A4", "McFarland & Co.", 1, "Includes bibliographical references...", "https://covers.openlibrary.org/b/id/1476102-L.jpg", 2022, false),
                createBook("Shop Talk", "Juwanda G. Ford", "Fiction", "0439568730", 32, "A4", "Scholastic", 1, null, "https://covers.openlibrary.org/b/id/1229497-L.jpg", 2016, false),
                createBook("Power tools recharged", "Joyce Kasman Valenza", "Forms", "0838908802", -1, "A4", "American Library Association", 1, "Includes bibliographical references.", "https://covers.openlibrary.org/b/id/637716-L.jpg", 2011, false),
                createBook("The captive bride", "Gilbert Morris", "Fiction", "076422915X", 238, "A4", "Bethany House", 1, null, "https://covers.openlibrary.org/b/id/519227-L.jpg", 2014, false),
                createBook("Under town", "Charles Ogden", "Fiction", "1582461260", 140, "A4", "Tricycle Press", 1, "Sinister twins...", "https://covers.openlibrary.org/b/id/845434-L.jpg", 2015, false),
                createBook("Little Pea", "Amy Krouse Rosenthal", "Fiction", "9780545515078", -1, "A4", "Chronicle Books", 1, "Little Pea hates...", "https://covers.openlibrary.org/b/id/601322-L.jpg", 2018, false),
                createBook("How to draw the life and times of James Monroe", "Miriam J. Gross", "Juvenile literature", "1404229825", 32, "A4", "PowerKids Press", 1, "Includes bibliographical references...", "https://covers.openlibrary.org/b/id/4243977-L.jpg", 2021, false),
                createBook("Julia Morgan", "Celeste Davidson Mannis", "Juvenile literature", "0670059641", -1, "A4", "Viking", 1, null, "https://covers.openlibrary.org/b/id/1333369-L.jpg", 2014, false),
                createBook("Freedom of speech and the press", "Ian C. Friedman", "Juvenile literature", "0816056625", -1, "A4", "Facts On File", 1, "Includes bibliographical references...", "https://covers.openlibrary.org/b/id/2650801-L.jpg", 2014, false),
                createBook("Business and legal forms for authors and self-publishers", "Tad Crawford", "Forms", "1581153953", 159, "A4", "Allworth Press", 1, "Includes index.", "https://covers.openlibrary.org/b/id/841651-L.jpg", 2010, false),
                createBook("The coast of Akron", "Adrienne Miller", "Fiction", "0374125120", 390, "A4", "Farrar, Straus and Giroux", 1, "Introduces the colorful members...", "https://covers.openlibrary.org/b/id/222828-L.jpg", 2016, false)
            );

            List<Book> savedBooks = bookRepository.saveAll(books);
            
            // Create branch inventory for each book
            List<BranchInventory> inventories = new ArrayList<>();
            for (Book book : savedBooks) {
                // Every book is available at Branch 1
                inventories.add(createInventory(book.getId(), 1L));
                
                // 50% chance available at Branch 2
                if (Math.random() > 0.5) {
                    inventories.add(createInventory(book.getId(), 2L));
                }
                
                // 30% chance available at Branch 3
                if (Math.random() > 0.7) {
                    inventories.add(createInventory(book.getId(), 3L));
                }
            }
            branchInventoryRepository.saveAll(inventories);
            
            log.info("Successfully populated {} books and {} inventory records.", savedBooks.size(), inventories.size());
        }
    }

    private Book createBook(String title, String author, String genre, String isbn, Integer pageCount, 
                            String paperType, String publisher, Integer shelfNumber, String description,
                            String imageUrl, Integer releaseYear, boolean isBestseller) {
        
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setIsbn(isbn);
        book.setPageCount(pageCount != null ? pageCount : 0);
        book.setPaperType(paperType);
        book.setPublisher(publisher);
        book.setShelfNumber(shelfNumber);
        book.setDescription(description);
        book.setImageUrl(imageUrl);
        book.setReleaseYear(releaseYear);
        book.setIsBestseller(isBestseller);
        book.setItemType(ItemType.BOOK);

        return book;
    }
    
    private BranchInventory createInventory(Long bookId, Long branchId) {
        return BranchInventory.builder()
                .itemId(bookId)
                .branchId(branchId)
                .status(CopyStatus.AVAILABLE)
                .build();
    }
}
