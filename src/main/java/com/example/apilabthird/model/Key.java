package com.example.apilabthird.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Key implements Serializable {

    private BigInteger p;
    private BigInteger g;
    private BigInteger privateB;
    private BigInteger publicB;
    private BigInteger s;

    public Key(BigInteger p, BigInteger g) {
        this.p = p;
        this.g = g;
    }
}
