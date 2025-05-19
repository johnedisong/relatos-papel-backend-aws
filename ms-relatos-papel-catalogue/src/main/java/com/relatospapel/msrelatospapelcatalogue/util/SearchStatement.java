package com.relatospapel.msrelatospapelcatalogue.util;

import com.relatospapel.msrelatospapelcatalogue.util.SearchOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SearchStatement {

    private String key;
    private Object value;
    private SearchOperation operation;
}
