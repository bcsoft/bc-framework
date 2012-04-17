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
	 * 根据Id查找模板的信息
	 * 
	 * @param id
	 * @return Map集合 当类型为Excel文件、Word文件或其它文件时，
	 *         返回Map集合的key值(模板类型type,模板名称name,文件名称fileName,文件流stream),
	 *         其中文件流为InputStream类型;
	 *         类型为文本文件、Html文件时，key值(模板类型type,模板名称name,模板内容content)。
	 */
	public Map<String, Object> findOneTemplateById(Integer id);

	/**
	 * 根据编码查找模板的信息
	 * 
	 * @param code
	 * @return Map集合 当类型为Excel文件、Word文件或其它文件时，
	 *         返回Map集合的key值(模板类型type,模板名称name,文件名称fileName,文件流stream),
	 *         其中文件流为InputStream类型;
	 *         类型为文本文件、Html文件时，key值(模板类型type,模板名称name,模板内容content)。
	 */
	public Map<String, Object> findOneTemplateByCode(String code);

	/**
	 * 根据id查找模板内容
	 * 
	 * @param id
	 * @return 模板内容
	 */
	public String findOneTemplateRtnContent(Integer id);

	/**
	 * 根据编码查找模板内容
	 * 
	 * @param code
	 * @return 模板内容
	 */
	public String findOneTemplateRtnContent(String code);

	/**
	 * 根据id查找文件
	 * 
	 * @param id
	 * @return
	 */
	public InputStream findOneTemplateRtnFile(Integer id);

	/**
	 * 根据编码查找文件
	 * 
	 * @param code
	 * @return
	 */
	public InputStream findOneTemplateRtnFile(String code);

	/**
	 * 返回相同文件名称的数量
	 * 
	 * @param type
	 * @param fileNmae
	 * @return
	 */
	public int countTemplateFileName(String fileName);

	/**
	 * 格式化指定的字符串信息
	 * 
	 * @param source
	 *            要格式化的字符串
	 */
	public String format(String source, Map<String, String> params);

	/**
	 * 获取模板格式化后的字符结果
	 * <p>
	 * 现只支持xml格式的Word文档、html文件、文本文件等，对doc和docx格式等非字符串类型的模板暂不支持
	 * </p>
	 * 
	 * @param templateCode
	 *            模板的编码
	 * @throws CoreException
	 *             模板不是字符串类型时
	 */
	public String formatTemplate(String templateCode, Map<String, Object> params);

	/**
	 * 将模板格式化并保存到指定的输出流
	 * <p>
	 * 现只支持xml格式的Word文档、html文件、文本文件等，对doc和docx格式等非字符串类型的模板暂不支持
	 * </p>
	 * 
	 * @param templateCode
	 *            模板的编码
	 * @param out
	 *            模板格式化后保存到的流
	 * @throws CoreException
	 *             模板不是xml格式的Word文档时
	 */
	public void formatTemplateTo(String templateCode,
			Map<String, Object> params, OutputStream out);
}
