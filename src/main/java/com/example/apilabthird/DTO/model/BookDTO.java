package com.example.apilabthird.DTO.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookDTO {

    private String id;
    private String author;
    private String title;
    private String genre;
    private String pages;
    private String weight;
}
