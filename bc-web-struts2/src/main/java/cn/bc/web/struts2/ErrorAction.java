/**
 *
 */
package cn.bc.web.struts2;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * 配置的Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ErrorAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  protected Log logger = LogFactory.getLog(getClass());

  public String html = "<div style='margin:10px;font-size:20px;font-weight:bold;'>功能建设中...</div>";

  public String todo() {
    return SUCCESS;
  }

  // 等待的时间,单位为秒
  public int t = 5;

  public String waiting() throws Exception {
    if (t > 0) {
      logger.warn("线程等待" + t + "秒后再继续...");
      Thread.sleep(t * 1000);
      logger.warn("线程等待完毕!");
    }

    html = "<div style='margin:10px;font-size:20px;font-weight:bold;'>祝贺你，加载完毕了！("
      + t + "秒)</div>";
    return SUCCESS;
  }
}