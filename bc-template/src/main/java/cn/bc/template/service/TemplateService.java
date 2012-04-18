package cn.bc.template.service;

import java.io.InputStream;
import java.io.OutputStream;
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
	 * 根据编码获取模板对象
	 * 
	 * @param code
	 * @return 指定编码的模板对象
	 */
	public Template loadByCode(String code);

	/**
	 * 判断指定的编码是否唯一
	 * 
	 * @param currentId
	 *            当前模板的id
	 * @param code
	 *            当前模板要使用的编码
	 * @return
	 */
	public boolean isUnique(Long currentId, String code);

	/**
	 * 获取指定编码模板对象的字符串内容信息
	 * <p>
	 * 如果模板为带附件类型的，而且此附件的内容是纯文本类型，则自动读取此附件的内容返回
	 * </p>
	 * 
	 * @param code
	 * @return 指定编码的模板对象
	 * @throws CoreException
	 *             模板不是字符类型时
	 */
	public String getContent(String code);

	/**
	 * 获取指定编码模板对象的附件文件流
	 * <p>
	 * 如果模板没有附件而是直接配置了字符内容，则自动将此内容生成一个内存流返回
	 * </p>
	 * 
	 * @param code
	 * @return 模板对象的附件文件流
	 */
	public InputStream getInputStream(String code);

	/**
	 * 获取模板格式化后的字符
	 * <p>
	 * 现只支持xml格式的Word文档、html文件、文本文件等，对doc和docx格式等非字符串类型的模板暂不支持
	 * </p>
	 * 
	 * @param code
	 *            模板的编码
	 * @throws CoreException
	 *             模板不是字符串类型时
	 */
	public String format(String code, Map<String, Object> args);

	/**
	 * 将模板格式化并保存到指定的输出流
	 * <p>
	 * 现只支持xml格式的Word文档、html文件、文本文件等，对doc和docx格式等非字符串类型的模板暂不支持
	 * </p>
	 * 
	 * @param code
	 *            模板的编码
	 * @param out
	 *            模板格式化后保存到的流
	 * @throws CoreException
	 *             模板不是xml格式的Word文档时
	 */
	public void formatTo(String code, Map<String, Object> args, OutputStream out);


	
}
