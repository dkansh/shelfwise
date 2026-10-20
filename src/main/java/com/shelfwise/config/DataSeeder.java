package com.shelfwise.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.shelfwise.events.EventStatus;
import com.shelfwise.events.LibraryEvent;
import com.shelfwise.events.LibraryEventRepository;
import com.shelfwise.fines.Fine;
import com.shelfwise.fines.FineRepository;
import com.shelfwise.fines.FineStatus;
import com.shelfwise.model.Author;
import com.shelfwise.model.Book;
import com.shelfwise.model.BookCopy;
import com.shelfwise.model.Category;
import com.shelfwise.model.CopyCondition;
import com.shelfwise.model.CopyStatus;
import com.shelfwise.model.LibraryBranch;
import com.shelfwise.model.Loan;
import com.shelfwise.model.LoanStatus;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.model.Review;
import com.shelfwise.repository.AuthorRepository;
import com.shelfwise.repository.BookCopyRepository;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.CategoryRepository;
import com.shelfwise.repository.LibraryBranchRepository;
import com.shelfwise.repository.LoanRepository;
import com.shelfwise.repository.MemberRepository;
import com.shelfwise.repository.ReviewRepository;

/**
 * Loads some sample data so the API isn't empty when you start it.
 */
@Component
@Order(1)
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LibraryBranchRepository branchRepository;

    @Autowired
    private BookCopyRepository copyRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LibraryEventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() > 0) {
            System.out.println("Data already there, skipping seeding");
            return;
        }

        // --- authors ---
        Author martin = authorRepository.save(author("Robert C. Martin", 1952, "American"));
        Author gamma = authorRepository.save(author("Erich Gamma", 1961, "Swiss"));
        Author lee = authorRepository.save(author("Harper Lee", 1926, "American"));
        Author orwell = authorRepository.save(author("George Orwell", 1903, "British"));
        Author tolkien = authorRepository.save(author("J.R.R. Tolkien", 1892, "British"));

        // --- categories ---
        Category fiction = categoryRepository.save(new Category("Fiction", "fiction", null));
        Category nonFiction = categoryRepository.save(new Category("Non-Fiction", "non-fiction", null));
        Category classics = categoryRepository.save(new Category("Classics", "classics", fiction));
        Category fantasy = categoryRepository.save(new Category("Fantasy", "fantasy", fiction));
        Category programming = categoryRepository.save(new Category("Programming", "programming", nonFiction));

        // --- books ---
        Book cleanCode = book("9780132350884", "Clean Code", "Robert C. Martin", 2008, "Programming", 3);
        cleanCode.setAuthorEntity(martin);
        cleanCode.setDescription("A handbook of agile software craftsmanship.");
        cleanCode.setPageCount(464);
        cleanCode.getCategories().add(programming);
        cleanCode = bookRepository.save(cleanCode);

        Book designPatterns = book("9780201633610", "Design Patterns", "Erich Gamma", 1994, "Programming", 2);
        designPatterns.setAuthorEntity(gamma);
        designPatterns.setDescription("Elements of reusable object-oriented software.");
        designPatterns.getCategories().add(programming);
        designPatterns = bookRepository.save(designPatterns);

        Book mockingbird = book("9780061120084", "To Kill a Mockingbird", "Harper Lee", 1960, "Fiction", 4);
        mockingbird.setAuthorEntity(lee);
        mockingbird.getCategories().add(classics);
        mockingbird = bookRepository.save(mockingbird);

        Book nineteenEightyFour = book("9780451524935", "1984", "George Orwell", 1949, "Fiction", 5);
        nineteenEightyFour.setAuthorEntity(orwell);
        nineteenEightyFour.setDescription("A dystopian novel about surveillance and totalitarianism.");
        nineteenEightyFour.getCategories().add(classics);
        nineteenEightyFour = bookRepository.save(nineteenEightyFour);

        Book hobbit = book("9780547928227", "The Hobbit", "J.R.R. Tolkien", 1937, "Fantasy", 1);
        hobbit.setAuthorEntity(tolkien);
        hobbit.getCategories().add(fantasy);
        hobbit = bookRepository.save(hobbit);

        Book catcher = bookRepository.save(book("9780316769488", "The Catcher in the Rye", "J.D. Salinger", 1951, "fiction", 2));

        // --- members ---
        Member alice = memberRepository.save(member("Alice Johnson", "alice@example.com", MembershipType.STANDARD, true, 200));
        Member bob = memberRepository.save(member("Bob Smith", "bob@example.com", MembershipType.PREMIUM, true, 5));
        Member carla = memberRepository.save(member("Carla Gomez", "carla@example.com", MembershipType.STUDENT, true, 300));
        memberRepository.save(member("Dan Lee", "dan@example.com", MembershipType.STANDARD, false, -10));

        // --- branches + copies ---
        LibraryBranch main = branchRepository.save(branch("Shelfwise Central", "MAIN", "1 Library Square", true));
        LibraryBranch east = branchRepository.save(branch("Eastside Branch", "EAST", "42 East Road", true));
        branchRepository.save(branch("Riverside Pop-up", "RIVER", "Riverside Park kiosk", null));

        copyRepository.save(copy("SW-000001", cleanCode, main, CopyStatus.ON_LOAN));
        copyRepository.save(copy("SW-000002", cleanCode, main, CopyStatus.AVAILABLE));
        copyRepository.save(copy("SW-000003", cleanCode, east, CopyStatus.AVAILABLE));
        copyRepository.save(copy("SW-000004", nineteenEightyFour, main, CopyStatus.ON_LOAN));
        copyRepository.save(copy("SW-000005", nineteenEightyFour, main, CopyStatus.AVAILABLE));
        copyRepository.save(copy("SW-000006", nineteenEightyFour, east, CopyStatus.AVAILABLE));
        copyRepository.save(copy("SW-000007", hobbit, east, CopyStatus.AVAILABLE));

        // --- some loan history ---
        LocalDate today = LocalDate.now();
        loanRepository.save(loan(cleanCode, alice, today.minusDays(5), today.plusDays(16), null, LoanStatus.ACTIVE));
        cleanCode.setAvailableCopies(cleanCode.getAvailableCopies() - 1);
        bookRepository.save(cleanCode);

        loanRepository.save(loan(nineteenEightyFour, bob, today.minusDays(30), today.minusDays(9), null, LoanStatus.ACTIVE));
        nineteenEightyFour.setAvailableCopies(nineteenEightyFour.getAvailableCopies() - 1);
        bookRepository.save(nineteenEightyFour);

        Loan lateHobbit = loan(hobbit, carla, today.minusDays(40), today.minusDays(19), today.minusDays(14), LoanStatus.RETURNED);
        lateHobbit.setFineAmount(1.25);
        lateHobbit = loanRepository.save(lateHobbit);

        loanRepository.save(loan(designPatterns, alice, today.minusDays(60), today.minusDays(39), today.minusDays(45), LoanStatus.RETURNED));
        loanRepository.save(loan(cleanCode, bob, today.minusDays(90), today.minusDays(69), today.minusDays(70), LoanStatus.RETURNED));
        loanRepository.save(loan(mockingbird, carla, today.minusDays(120), today.minusDays(99), today.minusDays(100), LoanStatus.RETURNED));

        Fine fine = new Fine();
        fine.setLoan(lateHobbit);
        fine.setMember(carla);
        fine.setAmount(new BigDecimal("1.25"));
        fine.setPaidAmount(BigDecimal.ZERO);
        fine.setStatus(FineStatus.UNPAID);
        fine.setReason("Late return of 'The Hobbit'");
        fine.setCreatedAt(LocalDateTime.now().minusDays(14));
        fineRepository.save(fine);

        // --- reviews ---
        reviewRepository.save(review(cleanCode, alice, 5, "Every developer should read this."));
        reviewRepository.save(review(cleanCode, bob, 3, "Good ideas but a bit preachy."));
        reviewRepository.save(review(nineteenEightyFour, bob, 4, "Scary how relevant it still is."));
        reviewRepository.save(review(hobbit, carla, 5, "Loved it!"));

        // --- events ---
        eventRepository.save(event("Classics Book Club", "This month: 1984 by George Orwell", main,
                LocalDateTime.now().plusDays(7).withHour(18).withMinute(0), 15));
        eventRepository.save(event("Kids Story Time", "Stories for 4-8 year olds", east,
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(0), 20));

        System.out.println("Seeded " + bookRepository.count() + " books, " + memberRepository.count()
                + " members, " + copyRepository.count() + " copies and " + loanRepository.count() + " loans");
    }

    private Author author(String name, int birthYear, String nationality) {
        Author a = new Author();
        a.setName(name);
        a.setBirthYear(birthYear);
        a.setNationality(nationality);
        return a;
    }

    private Book book(String isbn, String title, String author, int year, String genre, int copies) {
        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitle(title);
        b.setAuthor(author);
        b.setPublishedYear(year);
        b.setGenre(genre);
        b.setLanguage("English");
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        return b;
    }

    private Member member(String name, String email, MembershipType type, boolean active, int expiresInDays) {
        Member m = new Member();
        m.setName(name);
        m.setEmail(email);
        m.setMembershipType(type);
        m.setActive(active);
        m.setJoinedDate(new Date());
        m.setMembershipExpiry(LocalDate.now().plusDays(expiresInDays));
        return m;
    }

    private LibraryBranch branch(String name, String code, String address, Boolean active) {
        LibraryBranch b = new LibraryBranch();
        b.setName(name);
        b.setCode(code);
        b.setAddress(address);
        b.setOpeningHours("Mon-Sat 9:00-18:00");
        b.setActive(active);
        return b;
    }

    private BookCopy copy(String barcode, Book book, LibraryBranch branch, CopyStatus status) {
        BookCopy c = new BookCopy();
        c.setBarcode(barcode);
        c.setBook(book);
        c.setBranch(branch);
        c.setCondition(CopyCondition.GOOD);
        c.setStatus(status);
        c.setAcquiredDate(LocalDate.now().minusYears(1));
        return c;
    }

    private Loan loan(Book book, Member member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate,
                      LoanStatus status) {
        Loan l = new Loan();
        l.setBook(book);
        l.setMember(member);
        l.setLoanDate(loanDate);
        l.setDueDate(dueDate);
        l.setReturnDate(returnDate);
        l.setStatus(status);
        return l;
    }

    private Review review(Book book, Member member, int rating, String comment) {
        Review r = new Review();
        r.setBook(book);
        r.setMember(member);
        r.setRating(rating);
        r.setComment(comment);
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    private LibraryEvent event(String title, String description, LibraryBranch branch, LocalDateTime start,
                               int capacity) {
        LibraryEvent e = new LibraryEvent();
        e.setTitle(title);
        e.setDescription(description);
        e.setBranch(branch);
        e.setStartTime(start);
        e.setEndTime(start.plusHours(1));
        e.setCapacity(capacity);
        e.setStatus(EventStatus.SCHEDULED);
        e.setCreatedBy("seed");
        return e;
    }
}
