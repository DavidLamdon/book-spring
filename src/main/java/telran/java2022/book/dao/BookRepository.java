package telran.java2022.book.dao;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import telran.java2022.book.model.Book;

public interface BookRepository extends CrudRepository<Book, String> {

	@Query("select b from Book b")
	Stream<Book> findAllBooks();

	Stream<Book> findByPublisherPublisherName(String publisherName);

}
