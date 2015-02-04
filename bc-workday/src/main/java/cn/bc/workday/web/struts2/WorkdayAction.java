package cn.bc.workday.web.struts2;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.bc.BCConstants;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.workday.domain.Workday;
import cn.bc.workday.service.WorkdayService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


/**
 * 工作日管理表单action
 * 
 * @author LeeDane
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class WorkdayAction extends EntityAction<Long, Workday>{
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(getClass());
	
	private WorkdayService workdayService;
	public Map<Object,Object> dayoffList = null; //类别列表
	
	@Autowired
	public void setWorkdayService(WorkdayService workdayService){
		this.setCrudService(workdayService);
		this.workdayService = workdayService;
	}
	
	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：工作日管理员|系统管理员
		return !context.hasAnyRole(getText("key.role.bc.workday.manage"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(410)
				.setMinHeight(200).setMinWidth(150);
	}

    @Override
    protected boolean useFormPrint() {
        return false;
    }
  
    
    @Override
    protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
    	if(!this.isReadonly()){
    		// 确定按钮
    		ButtonOption saveButtonOption = new ButtonOption(
    				getText("workday.ok"), null, "bc.workday.save");
    		pageOption.addButton(saveButtonOption);
    	}
    }
    
    @Override
    protected void initForm(boolean editable) throws Exception {
    	this.dayoffList = getDayoffList();
    }
    
  //类别的下拉列表
  	private Map<Object, Object> getDayoffList() {
  		Map<Object, Object> map = new LinkedHashMap<Object, Object>();
  		map.put(true, "放假");
  		map.put(false, "上班");
  		return map;
  	}
  	
  	@Override
  	public String save() throws Exception {
 		
  		Workday e = this.getE();
  		boolean isCross;
  		if(e.isNew())
  			isCross = this.workdayService.checkDateIsCross(e.getFromDate(), e.getToDate());
  		else
  			isCross = this.workdayService.checkDateIsCross(e.getId(), e.getFromDate(), e.getToDate());
  		
  			if (isCross) {//出现交叉
  				JSONObject resultJson = new JSONObject();
  				String msg = "已经存在所输入日期的工作日配置，系统不允许日期出现重叠或交叉，请核实后重新输入！";
  				resultJson.put("success", false);
  				resultJson.put("msg", msg);
  				this.json = resultJson.toString();
  				return "json";

  			} else 		
  				return super.save();
  	}
	
}