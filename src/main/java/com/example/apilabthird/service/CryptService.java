package com.example.apilabthird.service;

import com.example.apilabthird.DTO.auth.AuthenticationRequest;
import com.example.apilabthird.DTO.auth.RegisterRequest;
import com.example.apilabthird.DTO.model.BookDTO;
import com.example.apilabthird.model.Book;
import com.example.apilabthird.model.Key;
import com.example.apilabthird.utils.Crypt;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class CryptService {

    private final Crypt crypt;
    private final RedisTemplate<String, Key> redisTemplate;
    private final HashOperations<String, String, Key> hashOperations;
    private final String TABLE_KEY = "TOKEN_KEY";

    public CryptService(Crypt crypt, RedisTemplate<String, Key> redisTemplate) {
        this.crypt = crypt;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public AuthenticationRequest decryptAuthenticationRequest(AuthenticationRequest authenticationRequest, String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Key key = hashOperations.get(TABLE_KEY, id);
        assert key != null;
        String username = crypt.decrypt(authenticationRequest.getUsername(), key.getS().toString());
        String password = crypt.decrypt(authenticationRequest.getPassword(), key.getS().toString());

        return AuthenticationRequest.builder()
                .username(username)
                .password(password)
                .build();
    }

    public RegisterRequest decryptRegisterRequest(RegisterRequest request, String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Key key = hashOperations.get(TABLE_KEY, id);
        assert key != null;
        String username = crypt.decrypt(request.getUsername(), key.getS().toString());
        String password = crypt.decrypt(request.getPassword(), key.getS().toString());
        String role = crypt.decrypt(request.getRole(), key.getS().toString());

        return RegisterRequest.builder()
                .username(username)
                .password(password)
                .role(role)
                .build();
    }

    public Book decryptBook(BookDTO bookDTO, String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Key key = hashOperations.get(TABLE_KEY, id);
        assert key != null;
        Long bookId;
        if (bookDTO.getId() != null) {
            bookId = Long.parseLong(crypt.decrypt(bookDTO.getId(), key.getS().toString()));
        } else
            bookId = null;

        String author = crypt.decrypt(bookDTO.getAuthor(), key.getS().toString());
        String title = crypt.decrypt(bookDTO.getTitle(), key.getS().toString());
        String genre = crypt.decrypt(bookDTO.getGenre(), key.getS().toString());
        Integer pages = Integer.parseInt(crypt.decrypt(bookDTO.getPages(), key.getS().toString()));
        Double weight = Double.parseDouble(crypt.decrypt(bookDTO.getWeight(), key.getS().toString()));

        return Book.builder()
                .id(bookId)
                .author(author)
                .title(title)
                .genre(genre)
                .pages(pages)
                .weight(weight)
                .build();
    }

    public BookDTO encryptBook(Book book, String id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        Key key = hashOperations.get(TABLE_KEY, id);
        assert key != null;
        String bookId = crypt.encrypt(book.getId().toString(), key.getS().toString());
        String author = crypt.encrypt(book.getAuthor(), key.getS().toString());
        String title = crypt.encrypt(book.getTitle(), key.getS().toString());
        String genre = crypt.encrypt(book.getGenre(), key.getS().toString());
        String pages = crypt.encrypt(book.getPages().toString(), key.getS().toString());
        String weight = crypt.encrypt(book.getWeight().toString(), key.getS().toString());

        return BookDTO.builder()
                .id(bookId)
                .author(author)
                .title(title)
                .genre(genre)
                .pages(pages)
                .weight(weight)
                .build();
    }


}

// 37FCA7EA1A5C9D756F31EC1ED17D8AF4
// 37FCA7EA1A5C9D756F31EC1ED17D8AF4
