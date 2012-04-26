package cn.bc.template.web.struts2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.struts2.FileEntityAction;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 模板表单Action
 * 
 * @author lbj
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TemplateAction extends FileEntityAction<Long, Template> {
	private static final long serialVersionUID = 1L;
	private TemplateService templateService;

	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.setCrudService(templateService);
		this.templateService = templateService;
	}

	@Override
	public boolean isReadonly() {

		// 模板管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 配置权限：模板管理员
		return !context.hasAnyRole(getText("key.role.bc.template"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			pageOption.addButton(new ButtonOption(getText("template.show.history.version"), null,
					"bc.templateForm.showVersion").setId("templateShowVersion"));
			pageOption.addButton(new ButtonOption(getText("label.preview.inline"), null,
					"bc.templateForm.inline").setId("templateInline"));
			if(editable)
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.templateForm.save").setId("templateSave"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinHeight(200).setMinWidth(300);
	}

	@Override
	protected void afterCreate(Template entity) {
		super.afterCreate(entity);
		entity.setType(Template.TYPE_EXCEL);
		// 内置 默认为否
		entity.setInner(false);
		//状态正常
		entity.setStatus(BCConstants.STATUS_ENABLED);
	}
	
	

	@Override
	public String save() throws Exception {
		Template template=this.getE();
		//状态：禁用
		if(template.getStatus()!=BCConstants.STATUS_ENABLED){
			this.beforeSave(template);
			this.templateService.getCrudDao().save(template);
			this.afterSave(template);
			return "saveSuccess";
		}
		//状态：正常
		this.beforeSave(template);
	    this.templateService.saveTpl(template);
	    this.afterSave(template);
	    return "saveSuccess";
	}



	public Integer type;//类型
	public Long tid;//模板id
	public String code;//编码
	public String version;//版本号

	// 检查编码与版本号唯一
	public String isUniqueCodeAndVersion() {
		Json json = new Json();
		boolean flag = this.templateService.isUniqueCodeAndVersion(this.tid, code,version);
		if (flag) {
			json.put("result", getText("template.save.code"));
			this.json = json.toString();
			return "json";
		} else {
			json.put("result", "save");
			this.json = json.toString();
			return "json";
		}
	}
	
	public String path;// 物理文件保存的相对路径
	public String content;//模板内容
	//---- 加载配置参数  ---开始--
	public String loadTplConfigParam(){
		Json json = new Json();
		//自定义文本
		if(type.equals(Template.TYPE_CUSTOM)){
			//保存参数的字符串
			String param=this.findParam(content);
			if(param==null){
				this.json=json.toString();
				return "json";
			}
			json.put("value", param);
		}
		
		this.json=json.toString();
		return "json";
	}
	
	/**查找字符串中的参数
	 * 
	 * @param 字符串text
	 * @return 参数1,参数2,参数3的字符串
	 */
	private String findParam(String text){
		if(text==null)
			return null;
		//临时保存参数的集合
		List<String> tempList=new ArrayList<String>();
		
		String[] textArr=text.split("\\$\\{");
		for(int i=0;i<textArr.length;i++){
			if(i>0){
				String tempParam="";
				//检测此字符串是否带有"}"
				int indx=textArr[i].indexOf("}");
				if(indx<0)//没
					return null;
				if(indx>0)
					tempParam=textArr[i].substring(0,indx);
				if(tempList.isEmpty()){
					tempList.add(tempParam);
				}else{
					boolean equal=false;
					for(String str:tempList){
						if(str.equals(tempParam)){
							equal=true;
							break;
						}
					}
					//不相同的加入集合
					if(!equal){
						tempList.add(tempParam);
					}
				}
			}
		}
		
		//保存参数的字符串
		String param="";
		for(int i=0;i<tempList.size();i++){
			param+=tempList.get(i);
			if((i+1)!=tempList.size()){
				param+=",";
			}
		}
		if(param.length()>0)
			return param; 
		return null;
	}
	//---- 加载配置参数  ---结束--
}
