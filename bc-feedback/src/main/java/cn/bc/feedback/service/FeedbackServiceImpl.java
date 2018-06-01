package cn.bc.feedback.service;

import cn.bc.BCConstants;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.feedback.dao.FeedbackDao;
import cn.bc.feedback.domain.Feedback;
import cn.bc.feedback.domain.Reply;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * 反馈service接口的实现
 *
 * @author dragon
 */
public class FeedbackServiceImpl extends DefaultCrudService<Feedback> implements
  FeedbackService {
  private FeedbackDao feedbackDao;
  private IdGeneratorService idGeneratorService;

  @Autowired
  public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
    this.idGeneratorService = idGeneratorService;
  }

  @Autowired
  public void setFeedbackDao(FeedbackDao feedbackDao) {
    this.setCrudDao(feedbackDao);
    this.feedbackDao = feedbackDao;
  }

  public Reply addReply(Long pid, String content) {
    // 添加回复
    Reply reply = new Reply();
    reply.setAuthor(SystemContextHolder.get().getUserHistory());
    reply.setSubject(null);
    reply.setContent(content);
    reply.setFileDate(Calendar.getInstance());
    reply.setStatus(BCConstants.STATUS_ENABLED);
    reply.setUid(this.idGeneratorService.next("reply.uid"));

    Feedback feedback = this.load(pid);
    reply.setFeedback(feedback);
    reply = this.feedbackDao.addReply(reply);
    return reply;
  }

  public void deleteReply(Long replyId) {
    this.feedbackDao.deleteReply(replyId);
  }
}
