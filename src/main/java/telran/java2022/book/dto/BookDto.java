package telran.java2022.book.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookDto {
	String isbn;
	String title;
	Set<AuthorDto>authors;
	String publisher;
}
