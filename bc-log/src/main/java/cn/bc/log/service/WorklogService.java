package cn.bc.log.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.log.domain.Worklog;

/**
 * 操作日志Service接口
 * 
 * @author dragon
 * 
 */
public interface WorklogService extends CrudService<Worklog> {
	/**
	 * 获取指定文档的工作日志
	 * 
	 * @param ptype
	 *            文档类型
	 * @param pid
	 *            文档标识号
	 * @return
	 */
	List<Worklog> find(String ptype, String pid);

	/**
	 * 插入一条操作日志信息
	 * 
	 * @param ptype
	 *            文档类型，不能为空
	 * @param pid
	 *            文档标识号，不能为空
	 * @param subject
	 *            标题，不能为空
	 * @param content
	 *            详细内容
	 * @return 插入成功后的操作日志信息
	 */
	Worklog save(String ptype, String pid, String subject, String content);
}
