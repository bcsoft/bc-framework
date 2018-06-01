/**
 *
 */
package cn.bc.option.web.struts2;

import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import com.opensymphony.xwork2.ActionSupport;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * 根据OptionGroup的Key选择所属OptionItem列表信息
 *
 * @author wis
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectOptionGroupByKeyAction extends ActionSupport {
  private static final long serialVersionUID = 1L;
  public String code; //对应数据库bc_option_group 的key_列
  public JSONArray optionItemList;
  public String json;
  private OptionService optionService;

  @Autowired
  public void setOptionService(OptionService optionService) {
    this.optionService = optionService;
  }

  public String execute() throws Exception {
    return SUCCESS;
  }

  public String selectOptionGroup() throws JSONException {
    // 批量加载可选项列表
    Map<String, List<Map<String, String>>> optionItems = this.optionService
      .findOptionItemByGroupKeys(new String[]{
        code
      });
    //需要的optionItem列表
    this.optionItemList = OptionItem.toLabelValues(optionItems
      .get(code));
    this.json = optionItemList.toString();
    return "json";
  }
}
