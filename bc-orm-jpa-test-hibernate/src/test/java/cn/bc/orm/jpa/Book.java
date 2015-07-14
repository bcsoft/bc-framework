package cn.bc.orm.jpa;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by dragon on 2015/7/1.
 */
@Entity
@Table(name = "bc_jpa_book")
public class Book implements Serializable {
	private Long id;
	private String code;
	private BookDetail detail;
	private BookDetail2 detail2;

	public Book() {
	}

	public Book(String code) {
		this();
		this.code = code;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	//@Transient
	@OneToOne
	@PrimaryKeyJoinColumn
	public BookDetail getDetail() {
		return detail;
	}
	public void setDetail(BookDetail detail) {
		this.detail = detail;
	}

	@OneToOne(cascade=CascadeType.ALL, optional = true)
	@PrimaryKeyJoinColumn
	public BookDetail2 getDetail2() {
		return detail2;
	}
	public void setDetail2(BookDetail2 detail2) {
		this.detail2 = detail2;
	}

	@Override
	public String toString() {
		return "{id: " + id + ", code: " + code + "}";
	}
}