package com.shelfwise.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shelfwise.model.Book;
import com.shelfwise.model.Member;
import com.shelfwise.model.MembershipType;
import com.shelfwise.repository.BookRepository;
import com.shelfwise.repository.MemberRepository;

/**
 * Loads some sample data so the API isn't empty when you start it.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookRepository.count() > 0) {
            System.out.println("Data already there, skipping seeding");
            return;
        }

        bookRepository.save(book("9780132350884", "Clean Code", "Robert C. Martin", 2008, "Programming", 3));
        bookRepository.save(book("9780201633610", "Design Patterns", "Erich Gamma", 1994, "Programming", 2));
        bookRepository.save(book("9780061120084", "To Kill a Mockingbird", "Harper Lee", 1960, "Fiction", 4));
        bookRepository.save(book("9780451524935", "1984", "George Orwell", 1949, "Fiction", 5));
        bookRepository.save(book("9780547928227", "The Hobbit", "J.R.R. Tolkien", 1937, "Fantasy", 1));
        bookRepository.save(book("9780316769488", "The Catcher in the Rye", "J.D. Salinger", 1951, "fiction", 2));

        memberRepository.save(member("Alice Johnson", "alice@example.com", MembershipType.STANDARD, true));
        memberRepository.save(member("Bob Smith", "bob@example.com", MembershipType.PREMIUM, true));
        memberRepository.save(member("Carla Gomez", "carla@example.com", MembershipType.STUDENT, true));
        memberRepository.save(member("Dan Lee", "dan@example.com", MembershipType.STANDARD, false));

        System.out.println("Seeded " + bookRepository.count() + " books and " + memberRepository.count() + " members");
    }

    private Book book(String isbn, String title, String author, int year, String genre, int copies) {
        Book b = new Book();
        b.setIsbn(isbn);
        b.setTitle(title);
        b.setAuthor(author);
        b.setPublishedYear(year);
        b.setGenre(genre);
        b.setTotalCopies(copies);
        b.setAvailableCopies(copies);
        return b;
    }

    private Member member(String name, String email, MembershipType type, boolean active) {
        Member m = new Member();
        m.setName(name);
        m.setEmail(email);
        m.setMembershipType(type);
        m.setActive(active);
        m.setJoinedDate(new Date());
        return m;
    }
}
