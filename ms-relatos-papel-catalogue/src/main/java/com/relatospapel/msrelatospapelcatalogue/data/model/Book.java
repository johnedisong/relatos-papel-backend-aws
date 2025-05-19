package com.relatospapel.msrelatospapelcatalogue.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.relatospapel.msrelatospapelcatalogue.controller.model.BookDto;
import com.relatospapel.msrelatospapelcatalogue.util.BookConst;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = BookConst.MSG_MANDATORY_TITLE)
    @Column(name = BookConst.TITLE, nullable = false)
    private String title;

    @NotBlank(message = BookConst.MSG_MANDATORY_AUTHOR)
    @Column(name = BookConst.AUTHOR, nullable = false)
    private String author;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = BookConst.PUBLICATION_DATE)
    private LocalDate publication_date;

    @Column(name = BookConst.CATEGORY)
    private String category;

    @NotBlank(message = BookConst.MSG_MANDATORY_ISBN)
    @Column(name = BookConst.ISBN, unique = true, nullable = false, length = 13)
    private String isbn;

    @DecimalMin(value = "1.0", message = BookConst.MSG_MIN_RATING)
    @DecimalMax(value = "5.0", message = BookConst.MSG_MAX_RATING)
    @Column(name = BookConst.RATING, precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = BookConst.VISIBILITY, columnDefinition = "boolean default true")
    private Boolean visibility = true;

    @Min(value = 0, message = BookConst.MSG_MIN_STOCK)
    @Column(name = BookConst.STOCK, columnDefinition = "int default 0")
    private Integer stock = 0;

    @Column(name = BookConst.CREATED_AT, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = BookConst.UPDATED_AT)
    private LocalDateTime updatedAt;

    public void update(BookDto bookDto) {
        this.title = bookDto.getTitle();
        this.author = bookDto.getAuthor();
        this.publication_date = LocalDate.parse(bookDto.getPublication_date());
        this.category = bookDto.getCategory();
        this.rating = bookDto.getRating();
        this.isbn = bookDto.getIsbn();
        this.visibility = Boolean.valueOf(bookDto.getVisibility());
        this.stock = Integer.valueOf(bookDto.getStock());
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    // Métodos para manejar la auditoría de fechas
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
