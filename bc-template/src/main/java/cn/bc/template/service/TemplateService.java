package cn.bc.template.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.Attach;
import cn.bc.identity.domain.ActorHistory;
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
	 *            如果含字符":"，则进行分拆，前面部分为编码，后面部分为版本号，如果没有字符":"，将获取当前状态为正常的版本
	 * @return 指定编码的模板对象
	 */
	public Template loadByCode(String code);

	/**
	 * 判断指定的编码与版本号是否唯一
	 * 
	 * @param currentId
	 *            当前模板的id
	 * @param code
	 *            当前模板要使用的编码
	 * @param version
	 *            当前模板要使用的版本号
	 * @return
	 */
	public boolean isUniqueCodeAndVersion(Long currentId, String code,
			String version);

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

	/**
	 * 保存方法，自动将同编码的另外一条状态为正常的模板转为禁用
	 * 
	 * @param template
	 * @return
	 */
	public void saveTpl(Template template);

	/**
	 * 查找模板分类
	 * 
	 * @return
	 */
	public List<Map<String, String>> findCategoryOption();

	/**
	 * 返回模板参数的格式化对应的Map
	 * 
	 * @param id
	 *            模板id
	 * @param mapFormatSql
	 *            格式化此模板参数详细配置sql中的占位符 格式为{占位名称:需要替换的值}
	 * @return
	 */
	Map<String, Object> getMapParams(Long id, Map<String, Object> mapFormatSql);

	/**
	 * 用指定的参数格式化指定模板，并将结果保存为附件
	 * 
	 * @param subject 附件主题 必填
	 *            
	 * @param code
	 *            模板编码 必填
	 * @param args
	 *            替换参数，注意：当模板带有参数时，若KEY相同，则会覆盖传入的参数 可输入 null值
	 * @param ptype
	 *            模块类型 必填
	 * @param puid
	 *            模块uid 必填
	 * @param author
	 *            创建附件的作者，可以输入null 当为null值时，则查找SystemContextHolder中的userhistory
	 *            当没SystemContextHolder时，默认admin
	 * @param formatParamSql
	 *            格式化模板参数sql的替换值
	 * 
	 * @return
	 */
	Attach getAttach(String subject, String code, Map<String, Object> args,
			String ptype, String puid, ActorHistory author,
			Map<String, Object> formatParamSql) throws Exception;

	/**
	 * 通过分类ID获得模板ID列表
	 * 
	 * @param pid 所属分类ID
	 * @return 
	 */
	List<Long> findTemplateIdsByCategoryIdForList(Long pid);
}
