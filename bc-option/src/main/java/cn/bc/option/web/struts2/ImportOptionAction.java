package cn.bc.option.web.struts2;

import cn.bc.docs.web.struts2.ImportDataAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * 导入选项数据的Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportOptionAction extends ImportDataAction {
  private static final long serialVersionUID = 1L;
  private final static Log logger = LogFactory
    .getLog(ImportOptionAction.class);

  @Override
  protected void importData(List<Map<String, Object>> data, JSONObject json, String fileType) throws JSONException {
    // TODO
    json.put("msg", "TODO: 成功导入" + data.size() + "条数据！");
    logger.fatal("TODO: ImportOptionAction.importData");
  }
}