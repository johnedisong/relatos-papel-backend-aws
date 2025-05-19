package com.relatospapel.msrelatospapelcatalogue.controller;

import com.relatospapel.msrelatospapelcatalogue.controller.model.BookDto;
import com.relatospapel.msrelatospapelcatalogue.controller.model.CreateBookRequest;
import com.relatospapel.msrelatospapelcatalogue.data.model.Book;
import com.relatospapel.msrelatospapelcatalogue.service.BooksServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Books Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre los Libros en una base de datos en MYSQL.")
public class BookController {

    private final BooksServiceImpl bookService;

    @GetMapping("/api/book")
    @Operation(
            operationId = "Obtener libros",
            description = "Operacion de lectura/consulta",
            summary = "Se devuelve una lista de todos los Libros almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<Book>> getBooks(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "title", description = "Nombre del libro. No tiene por que ser exacto", example = "quijote", required = false)
            @RequestParam(required = false) String title,
            @Parameter(name = "isbn", description = "El ISBN tiene por que ser exacto", example = "quijote", required = false)
            @RequestParam(required = false) String isbn,
            @Parameter(name = "author", description = "Nombre del Autor No tiene por que ser exacto", example = "miguel", required = false)
            @RequestParam(required = false) String author,
            @Parameter(name = "visibility", description = "Estado del producto true o false", example = "true", required = false)
            @RequestParam(required = false) Boolean visibility){

        List<Book> books = bookService.getBooks(title, author, isbn, visibility);

        return ResponseEntity.ok(Objects.requireNonNullElse(books, Collections.emptyList()));
    }

    @GetMapping("api/book/{id}")
    @Operation(
            operationId = "Obtener un libro",
            description = "Operacion de lectura",
            summary = "Se devuelve un libro a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    public ResponseEntity<Book> getBookById(@PathVariable String id){
        log.info("getBookById id: {}", id);
        Book book = bookService.getBookById(id);

        log.info("Book: " + book);
        if (book != null) {
            return ResponseEntity.ok(book);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("api/book")
    @Operation(
            operationId = "Insertar un Libro",
            description = "Operacion de escritura",
            summary = "Se crea un Libro a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateBookRequest.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos introducidos.")
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el libro con el identificador indicado.")
    public ResponseEntity<Book> createBook(@Valid @RequestBody CreateBookRequest book){
        Book bookCreated = bookService.createBook(book);
        if(bookCreated != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(bookCreated);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("api/book/{id}")
    @Operation(
            operationId = "Eliminar un Libro")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el Libro con el Identificador indicado")
    public ResponseEntity<Void> deleteBookById(@PathVariable String id){
        log.info("deleteBookById id: {}", id);
        Boolean removeBook = bookService.deleteBook(id);
        if(removeBook){
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("api/book/{bookId}")
    @Operation(
            operationId = "Modificar totalmente un libro",
            description = "Operacion de escritura",
            summary = "Se modifica totalmente un libro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = BookDto.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Libro no encontrado.")
    public ResponseEntity<Book> updateBookById(@PathVariable String bookId, @RequestBody BookDto body){
        log.info("updateBookById bookId: {}", bookId);
        Book updateBook = bookService.updateBook(bookId, body);
        if (updateBook != null) {
            return ResponseEntity.ok(updateBook);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("api/book/{bookId}")
    @Operation(
            operationId = "Modificar parcialmente un libro",
            description = "RFC 7386. Operacion de escritura",
            summary = "RFC 7386. Se modifica parcialmente un libro.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del libro a crear.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = String.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Libro inválido o datos incorrectos introducidos.")
    public ResponseEntity<Book> patchBookById(@PathVariable String bookId, @RequestBody String patchBody){
        log.info("patchBookById bookId: {}", bookId);
        Book patchBook = bookService.updateBook(bookId, patchBody);
        if (patchBook != null) {
            return ResponseEntity.ok(patchBook);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
