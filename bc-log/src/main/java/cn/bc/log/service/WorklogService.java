package cn.bc.log.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.log.domain.Worklog;

/**
 * 工作日志Service接口
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
}
