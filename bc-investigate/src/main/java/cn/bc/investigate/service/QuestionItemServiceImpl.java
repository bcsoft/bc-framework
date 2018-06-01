/**
 *
 */
package cn.bc.investigate.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.investigate.dao.QuestionItemDao;
import cn.bc.investigate.domain.QuestionItem;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 调查问卷的实现
 *
 * @author zxr
 */
public class QuestionItemServiceImpl extends DefaultCrudService<QuestionItem>
  implements QuestionItemService {
  private QuestionItemDao questionItemDao;

  public QuestionItemDao getQuestionItemDao() {
    return questionItemDao;
  }

  @Autowired
  public void setQuestionItemDao(QuestionItemDao questionItemDao) {
    this.questionItemDao = questionItemDao;
    this.setCrudDao(questionItemDao);
  }


}