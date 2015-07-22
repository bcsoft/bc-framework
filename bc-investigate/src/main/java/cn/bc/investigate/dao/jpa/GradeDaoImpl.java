/**
 *
 */
package cn.bc.investigate.dao.jpa;

import cn.bc.investigate.dao.GradeDao;
import cn.bc.investigate.domain.Grade;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 评分Dao的jpa实现
 *
 * @author zxr
 */
public class GradeDaoImpl extends JpaCrudDao<Grade> implements GradeDao {
}