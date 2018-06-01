package cn.bc.investigate.domain;

import cn.bc.core.EntityImpl;
import cn.bc.identity.domain.ActorHistory;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Set;

/**
 * 作答记录
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_RESPOND")
public class Respond extends EntityImpl {
  private static final long serialVersionUID = 1L;
  private Questionary questionary; // 所属调查
  private Calendar fileDate;// 作答时间
  private ActorHistory author;// 作答人
  private Set<Answer> answers;// 答案集
  private int score; // 分数（仅适用于网上考试）
  private boolean grade = false;// 标识该试卷是否已评分

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "PID", referencedColumnName = "ID")
  public Questionary getQuestionary() {
    return questionary;
  }

  public void setQuestionary(Questionary questionary) {
    this.questionary = questionary;
  }

  @Column(name = "FILE_DATE")
  public Calendar getFileDate() {
    return fileDate;
  }

  public void setFileDate(Calendar fileDate) {
    this.fileDate = fileDate;
  }

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "AUTHOR_ID", referencedColumnName = "ID")
  public ActorHistory getAuthor() {
    return author;
  }

  public void setAuthor(ActorHistory author) {
    this.author = author;
  }

  @OneToMany(mappedBy = "respond", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  public Set<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(Set<Answer> answers) {
    this.answers = answers;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public boolean isGrade() {
    return grade;
  }

  public void setGrade(boolean grade) {
    this.grade = grade;
  }

}
