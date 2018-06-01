/**
 *
 */
package cn.bc.investigate.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.AnswerDao;
import cn.bc.investigate.domain.Answer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户作答的内容的实现
 *
 * @author zxr
 */
public class AnswerServiceImpl extends DefaultCrudService<Answer> implements
  AnswerService {
  private AnswerDao answerDao;

  public AnswerDao getAnswerDao() {
    return answerDao;
  }

  @Autowired
  public void setAnswerDao(AnswerDao answerDao) {
    this.answerDao = answerDao;
    this.setCrudDao(answerDao);
  }

}