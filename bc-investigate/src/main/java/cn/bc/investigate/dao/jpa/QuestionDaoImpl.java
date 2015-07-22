/**
 *
 */
package cn.bc.investigate.dao.jpa;

import cn.bc.investigate.dao.QuestionDao;
import cn.bc.investigate.domain.Question;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 问题Dao的jpa实现
 *
 * @author zxr
 */
public class QuestionDaoImpl extends JpaCrudDao<Question> implements QuestionDao {
}