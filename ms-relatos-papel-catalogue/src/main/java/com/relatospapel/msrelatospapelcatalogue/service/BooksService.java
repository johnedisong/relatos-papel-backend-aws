package com.relatospapel.msrelatospapelcatalogue.service;

import com.relatospapel.msrelatospapelcatalogue.controller.model.BookDto;
import com.relatospapel.msrelatospapelcatalogue.controller.model.CreateBookRequest;
import com.relatospapel.msrelatospapelcatalogue.data.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BooksService {
    Book createBook(CreateBookRequest book);

    Book getBookById(String id);

    List<Book> getBooks(String title, String author, String isbn, Boolean visibility);

    Boolean deleteBook(String id);

    Book updateBook(String id, BookDto bookDetails);

    Book updateBook(String id, String bookDetails);

}