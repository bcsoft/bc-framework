package cn.bc.investigate.domain;

import cn.bc.core.EntityImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.Set;

/**
 * 问题
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_QUESTION")
public class Question extends EntityImpl {
  private static Log logger = LogFactory.getLog(Question.class);
  private static final long serialVersionUID = 1L;

  /**
   * 问题类型：单选题
   */
  public static final int TYPE_RADIO = 0;
  /**
   * 问题类型：多选题
   */
  public static final int TYPE_CHECKBOX = 1;
  /**
   * 问题类型：填空题
   */
  public static final int TYPE_FILL_IN = 2;
  /**
   * 问题类型：问答题
   */
  public static final int TYPE_QA = 3;

  /**
   * 布局配置的键：布局方向
   */
  public static final String CFG_KEY_ORIENTATION = "layout_orientation";
  /**
   * 布局配置的值：水平布局
   */
  public static final String CFG_VALUE_HORIZENTAL = "horizontal";
  /**
   * 布局配置的值：垂直布局
   */
  public static final String CFG_VALUE_VERTIVAL = "vertical";

  private Questionary questionary; // 所属调查表
  private int type; // 类型:见 TYPE_XXX 常数的定义
  private String subject; // 标题
  private int orderNo; // 排序号
  private boolean required = true; // 是否为必选题，默认为是
  private int score; // 分数（仅适用于网上考试）
  private boolean seperateScore = false; // (仅适用于网上考试的多选题)是否各个选项独立给分，默认为是，但答错任何一个选项将为0分
  private boolean grade = false;// 标识该问题管理是否需要进行评分

  /**
   * 特殊配置，使用json格式，如控制选项水平、垂直、多行布局，控制问答题输入框的默认大小等
   * <p>
   * 必须使用标准的Json格式进行配置，格式为：{layout_orientation:"horizontal|vertical",row:5}，
   * 相关常数见CFG_XXXX的定义，各个参数详细说明如下：
   * </p>
   * <ul>
   * <li>layout_orientation - 选项的布局方向，horizontal为水平布局，vertical为垂直布局</li>
   * <li>row - 问答题显示的行数，默认为5行</li>
   * </ul>
   */
  private String config;

  private Set<QuestionItem> items;// 问题项:单选题多选题的每个选项对应一个,问答题对应一个

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  @Column(name = "TYPE_")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  @Column(name = "ORDER_")
  public int getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(int orderNo) {
    this.orderNo = orderNo;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public Questionary getQuestionary() {
    return questionary;
  }

  public void setQuestionary(Questionary questionary) {
    this.questionary = questionary;
  }

  @OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy(value = "orderNo asc")
  public Set<QuestionItem> getItems() {
    return items;
  }

  public void setItems(Set<QuestionItem> items) {
    this.items = items;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  @Column(name = "SEPERATE_SCORE")
  public boolean isSeperateScore() {
    return seperateScore;
  }

  public void setSeperateScore(boolean seperateScore) {
    this.seperateScore = seperateScore;
  }

  public void setConfig(String config) {
    this.config = config;
  }

  public String getConfig() {
    return config;
  }

  public boolean isGrade() {
    return grade;
  }

  public void setGrade(boolean grade) {
    this.grade = grade;
  }

  /**
   * 获取布局配置的json对象，如果没有配置则返回空的Json对象
   *
   * @return
   */
  @Transient
  public JSONObject getConfigJson() {
    if (config == null || config.length() == 0) {
      return new JSONObject();
    }

    try {
      return new JSONObject(this.getConfig().replaceAll("\\s", " "));// 替换换行、回车等符号为空格
    } catch (JSONException e) {
      logger.error(e.getMessage(), e);
      return new JSONObject();
    }
  }
}
