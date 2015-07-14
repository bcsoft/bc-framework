package cn.bc.orm.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by dragon on 2015/7/1.
 */
@Entity
@Table(name = "bc_jpa_book_detail")
public class BookDetail implements Serializable {
	private Book book;
	private String info;

	public BookDetail() {
	}

	public BookDetail(Book book, String info) {
		this();
		this.book = book;
		this.info = info;
	}

	//@Column(name="ID") // @Column(s) not allowed on a @OneToOne property
	@Id
	//@MapsId
	@OneToOne(optional = false)
	@JoinColumn(name = "ID")
	//@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
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
		return "{id: " + book.getId() + ", info: " + info + "}";
	}
}