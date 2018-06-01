/**
 *
 */
package cn.bc.web.struts2;

import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 帮助Action
 *
 * @author dragon
 */
@Controller
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HelpAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  //private static Log logger = LogFactory.getLog(HelpAction.class);
  public String f;// 帮助文件的全子路径（不含文件扩展名，如“”）

  public String execute() throws Exception {
    if (f != null && f.length() > 0) {
      return "file";
    } else {
      return "anchor";
    }
  }
}