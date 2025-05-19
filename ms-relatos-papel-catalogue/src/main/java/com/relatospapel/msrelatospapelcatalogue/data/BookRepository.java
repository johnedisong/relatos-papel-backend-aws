package com.relatospapel.msrelatospapelcatalogue.data;

import com.relatospapel.msrelatospapelcatalogue.data.model.Book;
import com.relatospapel.msrelatospapelcatalogue.util.BookConst;
import com.relatospapel.msrelatospapelcatalogue.util.SearchCriteria;
import com.relatospapel.msrelatospapelcatalogue.util.SearchOperation;
import com.relatospapel.msrelatospapelcatalogue.util.SearchStatement;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final BookJpaRepository bookJpaRepository;
    private static final Logger log = LoggerFactory.getLogger(BookRepository.class);

    public List<Book> getBooks() {
        return bookJpaRepository.findAll();
    }

    public Book getBookById(String id) {
        return bookJpaRepository.findById(Long.valueOf(id)).get();
    }

    public Book save(Book book) {
        return bookJpaRepository.save(book);
    }

    public void delete(Book book) {
        bookJpaRepository.delete(book);
    }

    public List<Book> searchDirect(String title, String author, String isbn, Boolean visibility) {
        log.info("Direct search with parameters - title: '{}', author: '{}', isbn: '{}', visibility: '{}'",
                title, author, isbn, visibility);

        if (StringUtils.isNotBlank(title)) {
            log.info("Searching by title: '{}'", title);
            return bookJpaRepository.searchByTitleIgnoreCase(title);
        } else if (StringUtils.isNotBlank(author)) {
            log.info("Searching by author: '{}'", author);
            return bookJpaRepository.searchByAuthorIgnoreCase(author);
        } else if (StringUtils.isNotBlank(isbn)) {
            log.info("Searching by ISBN: '{}'", isbn);
            return bookJpaRepository.findByIsbn(isbn).map(List::of).orElse(List.of());
        } else if (visibility != null) {
            log.info("Searching by visibility: '{}'", visibility);
            return visibility ? bookJpaRepository.findByVisibilityTrue() : getBooks();
        } else {
            log.info("No specific criteria, returning all books");
            return getBooks();
        }
    }

    public List<Book> search(String title, String author, String isbn, Boolean visibility) {
        log.info("Creating search criteria with - title: '{}', author: '{}', isbn: '{}', visibility: '{}'",
                title, author, isbn, visibility);

        SearchCriteria<Book> spec = new SearchCriteria<>();

        if (StringUtils.isNotBlank(title)) {
            log.info("Adding title search criterion: '{}'", title);
            spec.add(new SearchStatement(BookConst.TITLE, title, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(author)) {
            log.info("Adding author search criterion: '{}'", author);
            spec.add(new SearchStatement(BookConst.AUTHOR, author, SearchOperation.MATCH));
        }

        if (StringUtils.isNotBlank(isbn)) {
            log.info("Adding ISBN search criterion: '{}'", isbn);
            spec.add(new SearchStatement(BookConst.ISBN, isbn, SearchOperation.EQUAL));
        }

        if (visibility != null) {
            log.info("Adding visibility search criterion: '{}'", visibility);
            spec.add(new SearchStatement(BookConst.VISIBILITY, visibility, SearchOperation.EQUAL));
        }

        log.info("Executing search with specifications");
        List<Book> results = bookJpaRepository.findAll(spec);
        log.info("Search returned {} results", results.size());

        return results;
    }
}