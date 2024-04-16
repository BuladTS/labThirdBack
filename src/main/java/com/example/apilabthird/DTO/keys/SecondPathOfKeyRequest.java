package com.example.apilabthird.DTO.keys;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecondPathOfKeyRequest {
    private BigInteger p;
    private BigInteger g;
    private BigInteger a;
}
