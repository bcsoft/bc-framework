/**
 *
 */
package cn.bc.acl.domain;

import cn.bc.identity.domain.FileEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 访问对象
 *
 * @author dragon
 */
@Entity
@Table(name = "BC_ACL_DOC")
public class AccessDoc extends FileEntityImpl {
  private static final long serialVersionUID = 1L;

  private String docId;// 文档标识
  private String docType;// 文档类型
  private String docName;// 文档名称

  @Column(name = "DOC_ID")
  public String getDocId() {
    return docId;
  }

  public void setDocId(String docId) {
    this.docId = docId;
  }

  @Column(name = "DOC_TYPE")
  public String getDocType() {
    return docType;
  }

  public void setDocType(String docType) {
    this.docType = docType;
  }

  @Column(name = "DOC_NAME")
  public String getDocName() {
    return docName;
  }

  public void setDocName(String docName) {
    this.docName = docName;
  }
}