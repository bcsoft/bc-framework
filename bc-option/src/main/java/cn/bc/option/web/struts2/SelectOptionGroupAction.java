/**
 *
 */
package cn.bc.option.web.struts2;

import cn.bc.option.domain.OptionGroup;
import cn.bc.option.service.OptionService;
import com.opensymphony.xwork2.ActionSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 选择所属分组信息
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectOptionGroupAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  public List<OptionGroup> es;
  private OptionService optionService;
  public long[] selected;// 当前选中项的id值，多个用逗号连接
  public boolean multiple;// 是否可以多选

  @Autowired
  public void setOptionService(OptionService optionService) {
    this.optionService = optionService;
  }

  public String execute() throws Exception {
    this.es = this.optionService.findOptionGroup();
    return SUCCESS;
  }
}
