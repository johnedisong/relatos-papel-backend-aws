package com.relatospapel.msrelatospapelcatalogue.data;

import org.springframework.data.jpa.repository.JpaRepository;
import com.relatospapel.msrelatospapelcatalogue.data.model.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


interface BookJpaRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByCategory(String category);

    List<Book> findByRatingGreaterThanEqual(double rating);

    List<Book> findByVisibilityTrue();

    List<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(String title, String author);

    // Método personalizado para búsqueda sin filtro de visibilidad
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> searchByTitleIgnoreCase(@Param("title") String title);

    // Método para buscar por autor sin filtro de visibilidad
    @Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<Book> searchByAuthorIgnoreCase(@Param("author") String author);
}