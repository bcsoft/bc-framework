package cn.bc.form.service;

import java.util.List;

import cn.bc.core.service.CrudService;
import cn.bc.form.domain.Field;
import cn.bc.form.domain.FieldLog;


/**
 * 审计日志Service接口
 * 
 * @author hwx
 * 
 */

public interface FieldLogService extends CrudService<FieldLog>{
	
	/**
	 * 获取审计日志集合
	 * @param form
	 * @return
	 */
	List<FieldLog> findList(Field field);
	
	/**
	 * 批量删除审计日志
	 * @param fieldLogs
	 */
	void delete(List<FieldLog> fieldLogs);

}
