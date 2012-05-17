package cn.bc.template.service;

import java.io.IOException;

import cn.bc.docs.domain.Attach;

/**
 * 从模板添加附件的接口
 * 
 * @author dragon
 * 
 */
public interface AddAttachFromTemplateService {
	/**
	 * 从模版添加附件
	 * 
	 * @param id
	 *            经济合同的id
	 * @param templateCode
	 *            模板编码
	 * @return 返回生成的附件信息
	 */
	Attach doAddAttachFromTemplate(Long id, String templateCode)
			throws IOException;
}
