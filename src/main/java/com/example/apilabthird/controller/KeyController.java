package com.example.apilabthird.controller;

import com.example.apilabthird.DTO.demo.TestDataString;
import com.example.apilabthird.DTO.keys.FirstPathOfKeyResponse;
import com.example.apilabthird.DTO.keys.SecondPartOfKeyResponse;
import com.example.apilabthird.DTO.keys.SecondPathOfKeyRequest;
import com.example.apilabthird.model.Key;
import com.example.apilabthird.service.KeyService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

@RestController
@RequestMapping("api/keys")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class KeyController {

    private final KeyService keyService;

    @GetMapping
    public ResponseEntity<Key> getKey(@RequestHeader HttpHeaders headers) {
        String authHeader = Objects.requireNonNull(headers.get("Authorization")).get(0);
        String token = authHeader.substring(7);
        return ResponseEntity.ok(keyService.getKey(token));
    }

    @PostMapping
    public ResponseEntity<Key> addKey(@RequestBody Key key, @RequestHeader HttpHeaders headers) {
        String authHeader = Objects.requireNonNull(headers.get("Authorization")).get(0);

        String token = authHeader.substring(7);
        log.info("Adding token " + token);
        return ResponseEntity.ok(keyService.addKey(token, key));
    }

    @GetMapping("/firstPath")
    public ResponseEntity<FirstPathOfKeyResponse> getFirstPathOfKey() {
        return ResponseEntity.ok(keyService.getFirstPathOfKey());
    }

//    @PostMapping("/secondPath")
//    public ResponseEntity<>
    @GetMapping("/cookie")
    public ResponseEntity<FirstPathOfKeyResponse> getCookie(
            HttpServletResponse response,
            @RequestHeader HttpHeaders headers
    ) {
        log.info(headers.toString());
        Cookie cookie = new Cookie("data",  "cookie");
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        response.addCookie(cookie);

        return ResponseEntity.ok(keyService.getFirstPathOfKey());
    }

    @GetMapping("/cookieGet")
    public ResponseEntity<FirstPathOfKeyResponse> putCookie(
            @CookieValue(value = "session_id") String data,
            @RequestHeader HttpHeaders headers
    ) {
        log.info("putCookie " + data);
        log.info(headers.toString());

        return ResponseEntity.ok(keyService.getFirstPathOfKey());
    }

    @PostMapping("/registerKey")
    public ResponseEntity<SecondPartOfKeyResponse> getSecondPathOfKey(
            @CookieValue(value = "session_id") String id,
            @RequestBody SecondPathOfKeyRequest request
    ) {
        log.info("session_id " + id);
        return ResponseEntity.ok(keyService.getSecondPartOfKey(id, request));
    }

    @GetMapping("/testCode")
    public ResponseEntity<TestDataString> testCode(
            @CookieValue(value = "session_id") String id
    ) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        return ResponseEntity.ok(keyService.testCode(id));
    }

    @PostMapping("/testEncode")
    public ResponseEntity<String> testDecode(
            @CookieValue(value = "session_id") String id,
            @RequestBody TestDataString dataString
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return ResponseEntity.ok(keyService.testDecode(dataString, id));
    }


    @PostMapping("/delSession")
    public void delSession(
            @CookieValue(value = "session_id") String id
    ) {
        log.info("Delete session: " + id);
        keyService.delKey(id);
    }

}
