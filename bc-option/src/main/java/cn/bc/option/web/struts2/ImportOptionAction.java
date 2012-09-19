package cn.bc.option.web.struts2;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.docs.web.struts2.ImportDataAction;

/**
 * 导入选项数据的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportOptionAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportOptionAction.class);

	@Override
	protected void importData(List<Map<String, Object>> data) {
		if (logger.isDebugEnabled()) {
			logger.debug("ImportOptionAction");
		}
	}

}
