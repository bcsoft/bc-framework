package cn.bc.identity.web.struts2;

import cn.bc.identity.service.IdGeneratorService;
import com.opensymphony.xwork2.ActionSupport;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

/**
 * Created by dragon on 2014/10/8.
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class IdGeneratorAction extends ActionSupport {
	public String type;
	public String pattern;
	public String json;

	@Autowired
	private IdGeneratorService idGeneratorService;

	// 获取月度流水号：yyyyMM-nnnn
	public String nextSN4Month() throws Exception {
		JSONObject json = new JSONObject();
		try {
			Assert.hasText(this.type, "type could not be empty");
			String sn;
			if (pattern == null || pattern.isEmpty())
				sn = this.idGeneratorService.nextSN4Month(this.type);
			else
				sn = this.idGeneratorService.nextSN4Month(this.type, this.pattern);

			json.put("success", true);
			json.put("sn", sn);
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}


		this.json = json.toString();
		return "json";
	}
}
