package cn.bc.log.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.log.dao.WorklogDao;
import cn.bc.log.domain.Worklog;

/**
 * 操作日志service接口的实现
 * 
 * @author dragon
 * 
 */
public class WorklogServiceImpl extends DefaultCrudService<Worklog> implements
		WorklogService {
	private WorklogDao worklogDao;

	@Autowired
	public void setWorklogDao(WorklogDao worklogDao) {
		this.worklogDao = worklogDao;
		this.setCrudDao(worklogDao);
	}

	public List<Worklog> find(String ptype, String pid) {
		return this.worklogDao.find(ptype, pid);
	}

	public Worklog save(String ptype, String pid, String subject, String content) {
		Assert.hasText(ptype, "ptype 不能为空！");
		Assert.hasText(pid, "pid 不能为空！");
		Assert.hasText(subject, "subject 不能为空！");
		Assert.hasText(content, "content 不能为空！");

		Worklog worklog = new Worklog();
		worklog.setFileDate(Calendar.getInstance());
		worklog.setAuthor(SystemContextHolder.get().getUserHistory());
		worklog.setPtype(ptype);
		worklog.setPid(pid);
		worklog.setSubject(subject);
		worklog.setContent(content);
		worklog.setType(Worklog.TYPE_SYSTEM);

		return this.save(worklog);
	}
}
