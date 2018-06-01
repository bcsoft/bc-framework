package cn.bc.orm.jpa;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 书本明细信息
 * 主键独立声明，但外键关联 Book 属性
 *
 * @author dragon 2015-07-29
 */
@Entity
@Table(name = "bc_jpa_book_detail")
public class BookDetail2 implements Serializable {
  private Long id;
  private Book book;
  private String publisher;

  public BookDetail2() {
  }

  // 这个绑定了hibernate框架 （@org.hibernate.annotations.Parameter）
  @Id
  @GeneratedValue(generator = "pkGenerator")
  @GenericGenerator(name = "pkGenerator", strategy = "foreign", parameters = @Parameter(name = "property", value = "book"))
  public Long getId() {
    return id;
  }

  private void setId(Long id) {
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

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  @Override
  public String toString() {
    return "{id: " + id + ", publisher: " + publisher + "}";
  }
}