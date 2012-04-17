package cn.bc.template.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.domain.Attach;
import cn.bc.template.dao.TemplateDao;
import cn.bc.template.domain.Template;

/**
 * Service接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateServiceImpl extends DefaultCrudService<Template> implements
		TemplateService {
	private static Log logger = LogFactory.getLog(TemplateServiceImpl.class);
	
	private TemplateDao templateDao;

	@Autowired
	public void setTemplateDao(TemplateDao templateDao) {
		this.templateDao = templateDao;
		this.setCrudDao(templateDao);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> findOneTemplateById(Integer id){
		return (Map<String, Object>) this.getTemplateInfo(id,null,null,null,new HashMap<String, Object>());
	}
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> findOneTemplateByCode(String code){
		return (Map<String, Object>) this.getTemplateInfo(null,code,null,null,new HashMap<String, Object>());
	}
	
	public String findOneTemplateRtnContent(Integer id){
		return  (String) this.getTemplateInfo(id,null,"",null,null);
	}
	
	public String findOneTemplateRtnContent(String code){
		return  (String) this.getTemplateInfo(null,code,"",null,null);
	}
	
	public InputStream findOneTemplateRtnFile(Integer id){
		
		return (InputStream) this.getTemplateInfo(id,null,null,"",null);
	}
	
	public InputStream findOneTemplateRtnFile(String code){
		
		return (InputStream) this.getTemplateInfo(null,code,null,"",null);
	}
	
	private Object getTemplateInfo(Integer id,String code,String content,String inputStream,Map<String,Object> m){
		List<Map<String,String>> templateList4map=null;
		
		if(code!=null){
			templateList4map=templateDao.findOneTemplateByCode(code);
		}else if(id!=null){
			templateList4map=templateDao.findOneTemplateById(id);
		}else{
			return null;
		}
		
		if(templateList4map==null||templateList4map.size()==0)
			return null;
		
		Map<String,String> template4map=templateList4map.get(0);
		if(template4map==null)
			return null;
		
		Integer type=Integer.parseInt(template4map.get("type"));
		if(type==null)
			return null;
		
		//excel、word、other
		if(type.equals(Template.TYPE_EXCEL)
					||type.equals(Template.KEY_CODE_WORD)
						||type.equals(Template.KEY_CODE_OTHER)){
			try {
				File dir=new File(Attach.DATA_REAL_PATH+Template.DATA_SUB_PATH);
				//没有此文件夹
				if(!dir.isDirectory())return null;
				
				//文件名称
				String filename=template4map.get("tfname");
				File fileDir=new File(Attach.DATA_REAL_PATH+Template.DATA_SUB_PATH+"/"+filename);
				//没有此文件
				if(!fileDir.exists())return null;
				
				//只返回文件流
				if(inputStream!=null){
					InputStream is=new FileInputStream(fileDir);
					return is;
				}else if(m!=null){
					m.put("type",type);
					m.put("name", template4map.get("name"));
					//文件名称
					m.put("fileName", filename);
					//文件流
					InputStream is=new FileInputStream(fileDir);
					m.put("stream", is);
					return m;
				}else{
					return null;
				}
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				//报错返回空
				return null;
			}
		}else{//text、html
			if(content!=null){
				if(template4map.get("content")!=null){
					return template4map.get("content").toString();
				}else{
					return null;
				}
			}else if(m!=null){
				m.put("type",type);
				m.put("name", template4map.get("name"));
				m.put("content",template4map.get("content"));
				return m;
			}else{
				return null;
			}
		}
	}

	public int countTemplateFileName(String fileName) {
		return this.templateDao.countTemplateFileName( fileName);
	}

	public String format(String source, Map<String, String> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public String formatTemplate(String templateCode, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public void formatTemplateTo(String templateCode,
			Map<String, Object> params, OutputStream out) {
		// TODO Auto-generated method stub
		
	}
}
