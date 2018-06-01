package cn.bc.form.domain;

import cn.bc.BCConstants;
import cn.bc.identity.domain.RichFileEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_FORM")
public class Form extends RichFileEntityImpl {
  private static final long serialVersionUID = 1L;
  /**
   * 状态：草稿
   */
  public static final int STATUS_DRAFT = BCConstants.STATUS_DRAFT;
  /**
   * 状态：正常
   */
  public static final int STATUS_ENABLED = BCConstants.STATUS_ENABLED;
  public static final String ATTACH_TYPE = Form.class.getSimpleName();
  private Long pid; //其他模块调用此模块时，该模块记录的id
  private String type;// 类别
  private String code;// 其他模块调用此模块时，使用的编码
  private String subject;// 标题
  private String tpl;// 模板编码
  private Float ver; // 版本
  private String description; // 备注
  private String ext01; // 扩展域1
  private String ext02; // 扩展域2
  private String ext03; // 扩展域3

  private List<Field> fields;// 表单字段列表

  @Transient
  public List<Field> getFields() {
    if (fields == null) fields = new ArrayList<Field>();
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  @Column(name = "DESC_")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExt01() {
    return ext01;
  }

  public void setExt01(String ext01) {
    this.ext01 = ext01;
  }

  public String getExt02() {
    return ext02;
  }

  public void setExt02(String ext02) {
    this.ext02 = ext02;
  }

  public String getExt03() {
    return ext03;
  }

  public void setExt03(String ext03) {
    this.ext03 = ext03;
  }

  @Column(name = "VER_")
  public Float getVer() {
    return ver;
  }

  public void setVer(Float ver) {
    this.ver = ver;
  }

  // 获取pid
  public Long getPid() {
    return pid;
  }

  public void setPid(Long pid) {
    this.pid = pid;
  }

  // 获取类别
  @Column(name = "TYPE_")
  public String getType() {
    return type;
  }

  // 设置类别
  public void setType(String type) {
    this.type = type;
  }

  // 获取编码
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  // 获取主题
  public String getSubject() {
    return subject;
  }

  // 设置主题
  public void setSubject(String subject) {
    this.subject = subject;
  }

  // 获取模板编码
  @Column(name = "TPL_")
  public String getTpl() {
    return tpl;
  }

  // 设置模板编码
  public void setTpl(String tpl) {
    this.tpl = tpl;
  }
}
