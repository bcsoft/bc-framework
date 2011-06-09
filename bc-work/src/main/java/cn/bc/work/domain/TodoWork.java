/**
 * 
 */
package cn.bc.work.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 待办事项
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_WORK_TODO")
public class TodoWork extends Base {
	private static final long serialVersionUID = 1L;
}
