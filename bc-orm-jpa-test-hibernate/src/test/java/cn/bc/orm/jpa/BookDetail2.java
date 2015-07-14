package cn.bc.orm.jpa;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by dragon on 2015/7/1.
 */
@Entity
@Table(name = "bc_jpa_book_detail")
public class BookDetail2 implements Serializable {
	private Long id;
	private Book book;
	private String info;

	public BookDetail2() {
	}

	@Id
	@GeneratedValue(generator = "pkGenerator")
	@GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "book"))
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "{id: " + id + ", info: " + info + "}";
	}
}