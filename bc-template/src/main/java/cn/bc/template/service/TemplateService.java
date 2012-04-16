package cn.bc.template.service;

import java.io.InputStream;
import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.template.domain.Template;


/**
 * Service接口
 * 
 * @author dragon
 * 
 */
public interface TemplateService extends CrudService<Template> {
	/**
	 * 根据Id查找模板的信息
	 * @param id
	 * @return Map集合
	 * 		当类型为Excel文件、Word文件或其它文件时，
	 * 		返回Map集合的key值(模板类型type,模板名称name,文件名称fileName,文件流stream),
	 * 		其中文件流为InputStream类型;
	 * 		类型为文本文件、Html文件时，key值(模板类型type,模板名称name,模板内容content)。
	 */
	public Map<String,Object> findOneTemplateById(Integer id);
	
	/**
	 * 根据编码查找模板的信息
	 * @param code
	 * @return Map集合
	 * 		当类型为Excel文件、Word文件或其它文件时，
	 * 		返回Map集合的key值(模板类型type,模板名称name,文件名称fileName,文件流stream),
	 * 		其中文件流为InputStream类型;
	 * 		类型为文本文件、Html文件时，key值(模板类型type,模板名称name,模板内容content)。
	 */
	public Map<String,Object> findOneTemplateByCode(String code);
	
	/**
	 * 根据id查找模板内容
	 * @param id
	 * @return 模板内容
	 */
	public String findOneTemplateRtnContent(Integer id);
	
	/**
	 * 根据编码查找模板内容
	 * @param code
	 * @return 模板内容
	 */
	public String findOneTemplateRtnContent(String code);
	
	/**
	 * 根据id查找文件
	 * @param id
	 * @return 
	 */
	public InputStream findOneTemplateRtnFile(Integer id);
	
	/**
	 * 根据编码查找文件
	 * @param code
	 * @return 
	 */
	public InputStream findOneTemplateRtnFile(String code);
	
	/**
	 * 返回相同文件名称的数量
	 * @param type
	 * @param fileNmae
	 * @return
	 */
	public int countTemplateFileName(String fileName);
}
