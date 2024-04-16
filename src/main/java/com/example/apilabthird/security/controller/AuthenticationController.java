package com.example.apilabthird.security.controller;

import com.example.apilabthird.DTO.auth.AuthenticationRequest;
import com.example.apilabthird.DTO.auth.AuthenticationResponse;
import com.example.apilabthird.DTO.auth.RegisterRequest;
import com.example.apilabthird.security.service.AuthenticationService;
import com.example.apilabthird.service.CryptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CryptService cryptService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            @CookieValue(value = "session_id") String id
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        log.debug("Register: " + request);
        return ResponseEntity.ok(authenticationService.register(cryptService.decryptRegisterRequest(request, id)));
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            @CookieValue(value = "session_id") String id
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        log.debug("Authentication " + request);
        return ResponseEntity.ok(authenticationService.authenticate(cryptService.decryptAuthenticationRequest(request ,id)));
    }

    @GetMapping("/check-token")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.ok("Token is valid");
    }
}
