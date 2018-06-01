package cn.bc.investigate.domain;

import cn.bc.core.EntityImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;

import javax.persistence.*;

/**
 * 问题项
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_QUESTION_ITEM")
public class QuestionItem extends EntityImpl {
  private static Log logger = LogFactory.getLog(QuestionItem.class);
  private static final long serialVersionUID = 1L;

  private Question question; // 所属问题
  private int orderNo; // 排序号
  private String subject; // 单选多选题显示的选项文字,如果为问答题则为默认填写的内容
  private int score; // 分数（仅适用于网上考试）
  private boolean standard; // 标准答案

  /**
   * 用于填空题的标准答案配置
   * <p>
   * 使用json数组格式[{},...]，每个json元素格式为{key:
   * '占位符',value:'标准答案',score:分数}，各个参数详细说明如下：
   * </p>
   * <ul>
   * <li>key - 填空题内的某个占位符</li>
   * <li>value - 占位符对应的标准答案</li>
   * <li>score - 填对此空位的得分</li>
   * </ul>
   */
  private String config;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public Question getQuestion() {
    return question;
  }

  public void setQuestion(Question question) {
    this.question = question;
  }

  @Column(name = "ORDER_")
  public int getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public boolean isStandard() {
    return standard;
  }

  public void setStandard(boolean standard) {
    this.standard = standard;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getConfig() {
    return config;
  }

  /**
   * 获取特殊配置的json对象，如果没有配置则返回空的Json对象
   *
   * @return
   */
  @Transient
  public JSONArray getConfigJsonArray() {
    if (config == null || config.length() == 0) {
      return new JSONArray();
    }

    try {
      return new JSONArray(this.getConfig().replaceAll("\\s", " "));// 替换换行、回车等符号为空格
    } catch (JSONException e) {
      logger.error(e.getMessage(), e);
      return new JSONArray();
    }
  }
}
