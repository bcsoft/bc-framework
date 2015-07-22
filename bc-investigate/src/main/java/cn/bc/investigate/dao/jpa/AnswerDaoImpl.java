/**
 *
 */
package cn.bc.investigate.dao.jpa;

import cn.bc.investigate.dao.AnswerDao;
import cn.bc.investigate.domain.Answer;
import cn.bc.orm.jpa.JpaCrudDao;

/**
 * 用户作答的内容Dao的jpa实现
 *
 * @author zxr
 */
public class AnswerDaoImpl extends JpaCrudDao<Answer> implements AnswerDao {
}