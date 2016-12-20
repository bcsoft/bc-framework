package cn.bc.orm.jpa.po;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 书本明细信息
 * 主键直接关联 Book 对象
 * @author dragon 2015-07-29
 */
@Entity
@Table(name = "t_jpa_book_detail")
public class BookDetail implements Serializable {
    private Book id; // 所属书
    private String publisher;// 出版社

    public BookDetail() {
    }

    @Id
    @OneToOne(optional = false)
    @JoinColumn(name = "ID")
    public Book getId() {
        return id;
    }

    public void setId(Book book) {
        this.id = book;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "{id: " + id.getId() + ", publisher: " + getPublisher() + "}";
    }
}