package com.example.apilabthird.controller;

import com.example.apilabthird.DTO.model.BookDTO;
import com.example.apilabthird.model.Book;
import com.example.apilabthird.service.BookService;
import com.example.apilabthird.service.CryptService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;
    private final CryptService cryptService;

    @GetMapping
    public ResponseEntity<List<BookDTO>> getBooks(
            @CookieValue(value = "session_id") String sessionId
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        List<Book> books = bookService.getAll();
        List<BookDTO> bookDTOS = new ArrayList<>();
        for (Book book : books)
            bookDTOS.add(cryptService.encryptBook(book, sessionId));
        log.debug("All books");
        return new ResponseEntity<>(bookDTOS, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(
            @PathVariable Long id,
            @CookieValue(value = "session_id") String sessionId
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        Book book = bookService.getBook(id);

        return new ResponseEntity<>(cryptService.encryptBook(book, sessionId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestBody BookDTO bookDTO,
            @CookieValue(value = "session_id") String id
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Book decryptedBook = cryptService.decryptBook(bookDTO, id);
        log.debug("Book encrypted " + bookDTO);
        return new ResponseEntity<>(bookService.createBook(decryptedBook), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Book> deleteBook(@PathVariable Long id) {
        return new ResponseEntity<>(bookService.deleteBook(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestBody JsonNode bookDTO,
            @CookieValue(value = "session_id") String sessionId
    ) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        log.info("Updating book: " + bookDTO);
        return new ResponseEntity<>(HttpStatus.OK);
//        Book decryptedBook = cryptService.decryptBook(bookDTO, sessionId);
//        return new ResponseEntity<>(bookService.updateBook(id, decryptedBook), HttpStatus.OK);
    }
}
