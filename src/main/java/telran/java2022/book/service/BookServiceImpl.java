package telran.java2022.book.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java2022.book.dao.AuthorRepository;
import telran.java2022.book.dao.BookRepository;
import telran.java2022.book.dao.PublisherRepository;
import telran.java2022.book.dto.AuthorDto;
import telran.java2022.book.dto.BookDto;
import telran.java2022.book.dto.exeptions.EntityNotFoundException;
import telran.java2022.book.model.Author;
import telran.java2022.book.model.Book;
import telran.java2022.book.model.Publisher;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
	final BookRepository bookRepository;
	final AuthorRepository authorRepository;
	final PublisherRepository publisherRepository;
	final ModelMapper modelMapper;

	@Override
	@Transactional
	public boolean addBook(BookDto bookDto) {
		if (bookRepository.existsById(bookDto.getIsbn())) {
			return false;
		}
		Publisher publisher = publisherRepository.findById(bookDto.getPublisher()).orElse(publisherRepository.save(new Publisher(bookDto.getPublisher())));
		
		Set<Author> authors = bookDto.getAuthors().stream()
										.map(a -> authorRepository.findById(a.getName())
										.orElse(authorRepository.save(new Author(a.getName(), a.getBirthDate()))))
										.collect(Collectors.toSet());
		Book book = new Book(bookDto.getIsbn(), bookDto.getTitle(), authors, publisher);
		bookRepository.save(book);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public BookDto findBookByIsbn(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(()->new EntityNotFoundException());
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto removeBook(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(()->new EntityNotFoundException());
		bookRepository.deleteById(isbn);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto updateBook(String isbn, String title) {
		Book book = bookRepository.findById(isbn).orElseThrow(()->new EntityNotFoundException());
		book.setTitle(title);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<BookDto> findBooksByAuthor(String authorName) {
		List<Book>books = bookRepository.findAllBooks()
										.collect(Collectors.toList());
		List<Book>res = getAllBooksFromAuthor(books, authorName);
		return res.stream()
					.map(n->modelMapper.map(n, BookDto.class))
					.collect(Collectors.toList());
	}


	private List<Book> getAllBooksFromAuthor(List<Book> books, String authorName) {
		List<Book>res = new ArrayList<>(); 
		for (Book book : books) {//использовал цикл только для того что бы в стриме пока докопаюсь до автора  книга не пропала.
			long count = book.getAuthors().stream()
							.filter(n->n.getName().equals(authorName))
							.count();
			if(count>0) {
				res.add(book);
			}
		}
		return res;
		
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<BookDto> findBooksByPublisher(String publisherName) {
		return bookRepository.findByPublisherPublisherName(publisherName)
							.map(b->modelMapper.map(b, BookDto.class))
							.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<AuthorDto> findBookAuthors(String isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(()->new EntityNotFoundException());
		return book.getAuthors().stream()
								.map(a->modelMapper.map(a, AuthorDto.class))
								.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<String> findPublishersByAuthor(String authorName) {
		List<Book>books = bookRepository.findAllBooks()
										.collect(Collectors.toList());
		return getAllBooksFromAuthor(books, authorName).stream()
														.map(n->n.getPublisher().getPublisherName())
														.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public AuthorDto removeAuthor(String authorName) {
		return null;
	}

}
