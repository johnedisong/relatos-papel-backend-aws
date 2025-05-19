package com.relatospapel.msrelatospapelcatalogue.controller.model;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BookDto {

    private String title;
    private String author;
    private String publication_date;
    private String category;
    private String isbn;
    private BigDecimal rating;
    private String visibility;
    private String stock;
    private String created_at;
    private String updated_at;

}
