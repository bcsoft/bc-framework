package cn.bc.orm.jpa;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 书本
 *
 * @author dragon 2015-07-29
 */
@Entity
@Table(name = "bc_jpa_book")
public class Book extends IdEntity<Long> implements Serializable {
    private String name;// 书名

    public Book() {
    }

    public Book(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //@Transient
//	@OneToOne
//	@PrimaryKeyJoinColumn
//	public BookDetail getDetail() {
//		return detail;
//	}
//	public void setDetail(BookDetail detail) {
//		this.detail = detail;
//	}
//
//	@OneToOne(cascade=CascadeType.ALL, optional = true)
//	@PrimaryKeyJoinColumn
//	public BookDetail2 getDetail2() {
//		return detail2;
//	}
//	public void setDetail2(BookDetail2 detail2) {
//		this.detail2 = detail2;
//	}

    @Override
    public String toString() {
        return "{id: " + getId() + ", name: " + getName() + "}";
    }
}