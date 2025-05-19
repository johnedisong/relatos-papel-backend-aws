package com.relatospapel.msrelatospapelcatalogue.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {

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
