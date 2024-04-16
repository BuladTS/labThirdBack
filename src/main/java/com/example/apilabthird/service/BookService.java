package com.example.apilabthird.service;

import com.example.apilabthird.model.Book;
import com.example.apilabthird.repository.BookRepository;
import com.example.apilabthird.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty())
            throw new ResourceNotFoundException("Book not found");
        return book.get();
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            bookRepository.deleteById(id);
            return book.get();
        }
        throw new ResourceNotFoundException("Book with id " + id + " not found");
    }

    public Book updateBook(Long id, Book book) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isEmpty())
            throw new ResourceNotFoundException("Book with id " + id + " not found");
        Book bookNotUpdated = optionalBook.get();
        if (book.getAuthor() != null && !book.getAuthor().isEmpty())
            bookNotUpdated.setAuthor(book.getAuthor());
        if (book.getGenre() != null && !book.getGenre().isEmpty())
            bookNotUpdated.setGenre(book.getGenre());
        if (book.getWeight() != null && book.getWeight() > 0)
            bookNotUpdated.setWeight(book.getWeight());
        if (book.getPages() != null && book.getPages() > 0)
            bookNotUpdated.setPages(book.getPages());
        bookRepository.save(bookNotUpdated);
        return bookNotUpdated;
    }
}
