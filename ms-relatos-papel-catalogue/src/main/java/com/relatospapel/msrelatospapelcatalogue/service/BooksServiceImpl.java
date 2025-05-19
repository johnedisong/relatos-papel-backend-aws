package com.relatospapel.msrelatospapelcatalogue.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.relatospapel.msrelatospapelcatalogue.controller.model.BookDto;
import com.relatospapel.msrelatospapelcatalogue.controller.model.CreateBookRequest;
import com.relatospapel.msrelatospapelcatalogue.data.BookRepository;
import com.relatospapel.msrelatospapelcatalogue.data.model.Book;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;


    @Override
    public Book createBook(CreateBookRequest bookRequest) {
        if (bookRequest != null
                && StringUtils.hasLength(bookRequest.getTitle().trim())
                && StringUtils.hasLength(bookRequest.getAuthor().trim())
                && StringUtils.hasLength(bookRequest.getPublication_date().trim())
                && StringUtils.hasLength(bookRequest.getCategory().trim())
                && StringUtils.hasLength(bookRequest.getIsbn().trim())
                && StringUtils.hasLength(bookRequest.getVisibility().trim())
                && StringUtils.hasLength(bookRequest.getStock().trim())
        ) {
            Book book = Book.builder()
                    .title(bookRequest.getTitle())
                    .author(bookRequest.getAuthor())
                    .publication_date(LocalDate.parse(bookRequest.getPublication_date()))
                    .category(bookRequest.getCategory())
                    .isbn(bookRequest.getIsbn())
                    .rating(bookRequest.getRating())
                    .visibility(Boolean.valueOf(bookRequest.getVisibility()))
                    .stock(Integer.valueOf(bookRequest.getStock()))
                    .build();

            return bookRepository.save(book);

        } else {
            return null;
        }
    }

    @Override
    public Book getBookById(String id) {
        return bookRepository.getBookById(id);
    }

    @Override
    public List<Book> getBooks(String title, String author, String isbn, Boolean visibility) {
        log.info("Fetching books with parameters - title: '{}', author: '{}', isbn: '{}', visibility: '{}'",
                title, author, isbn, visibility);

        // Verificar si los parámetros tienen contenido o son nulos/vacíos
        boolean hasTitleFilter = StringUtils.hasLength(title);
        boolean hasAuthorFilter = StringUtils.hasLength(author);
        boolean hasIsbnFilter = StringUtils.hasLength(isbn);
        boolean hasVisibilityFilter = visibility != null;

        log.info("Filters applied - title: {}, author: {}, isbn: {}, visibility: {}",
                hasTitleFilter, hasAuthorFilter, hasIsbnFilter, hasVisibilityFilter);

        List<Book> books;
        if (hasTitleFilter || hasAuthorFilter || hasIsbnFilter || hasVisibilityFilter) {
            log.info("Using direct search to bypass potential issues...");
            books = bookRepository.searchDirect(title, author, isbn, visibility);
        } else {
            log.info("Fetching all books without filters...");
            books = bookRepository.getBooks();
        }

        log.info("Found {} books", books != null ? books.size() : 0);
        return books;
    }


    @Override
    public Book updateBook(String id, String details) {
        Book book = bookRepository.getBookById(id);
        if (book != null) {
            try {
                JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(details));
                JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(book)));
                Book patched = objectMapper.treeToValue(target, Book.class);
                bookRepository.save(patched);
                return patched;
            } catch (JsonProcessingException | JsonPatchException e) {
                log.error("Error updating product {}", id, e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public Book updateBook(String id, BookDto bookDetails) {
        Book book = bookRepository.getBookById(id);
        if (book != null) {
            book.update(bookDetails);
            bookRepository.save(book);
            return book;
        } else {
            return null;
        }
    }


    @Override
    public Boolean deleteBook(String id) {
        Book book = bookRepository.getBookById(id);
        if (book != null) {
            bookRepository.delete(book);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }


    }

}
