package library.project.services;

import library.project.models.Book;
import library.project.models.Person;
import library.project.repositories.BookRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findOne(int id) {
        Optional<Book> foundPerson = bookRepository.findById(id);
        return foundPerson.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    // получаем человека, которому принадлежит книга с указанным id
    public Person getBookOwner(int id) {
        // Здесь Hibernate.initialize() не нужен, так как владелец (сторона One) загружается не лениво
        return bookRepository.findById(id).map(Book::getPerson).orElse(null);

    }

    // Освобождает книгу (этот метод вызывается, когда человек возвращает книгу в библиотеку)
    public void release(int id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Hibernate.initialize(book.get().getPerson());

            book.get().setPerson(null);
        }
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    public void assign(int id, Person selectedPerson) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            Hibernate.initialize(book.get().getPerson());

            book.get().setPerson(selectedPerson);
        }
    }

}