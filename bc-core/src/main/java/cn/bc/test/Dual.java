package cn.bc.test;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 模拟oracle dual功能的在hibernate hql中使用的视图
 *
 * @author dragon
 */
@Entity
@Table(name = "bc_dual")
public class Dual implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID")
  private Long id = null;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
