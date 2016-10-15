package cn.bc.identity.web.rest;

import cn.bc.core.exception.NotExistsException;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import com.owlike.genson.Genson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.Path;
import java.util.Map;

/**
 * 职务
 *
 * @author dragon 2016-07-14
 */
@Singleton
@Named
public class DutyResourceImpl implements DutyResource {
	private final static Logger logger = LoggerFactory.getLogger(DutyResourceImpl.class);

	public DutyResourceImpl() {
		logger.debug("this={}", this);
	}

	@PostConstruct
	private void init() {
		logger.debug("init: service={}", service);
	}

	@Inject
	private DutyService service;

	@Override
	public String data(int pageNo, int pageSize, String search) {
		Map<String, Object> data = service.data(pageNo, pageSize, search);
		return new Genson().serialize(data);
	}

	@Override
	public Duty get(Long id) {
		Duty e = service.load(id);
		if(e == null) throw new NotExistsException("所要获取的职务信息不存在！");
		return e;
	}

	@Override
	public String create(String code, String name) {
		String id = saveOrUpdate(null, code, name);
		return "{\"id\": " + id + "}";
	}

	@Override
	public String update(Long id, String code, String name) {
		saveOrUpdate(id, code, name);
		return "{\"id\": " + id + "}";
	}

	@Override
	public void delete(Long id) {
		service.delete(id);
	}

	@Override
	public void deleteBatch(String ids) {
		service.delete(StringUtils.stringArray2LongArray(ids.split(",")));
	}

	private String saveOrUpdate(Long id, String code, String name) {
		logger.debug("id={}, code={}, name={}", id, code, name);
		Duty e;
		if (id != null) {
			e = service.load(id);
			if (e == null) throw new NotExistsException("所要更新的职务信息不存在！");
		} else {
			e = new Duty();
		}
		e.setCode(code);
		e.setName(name);
		service.save(e);
		return String.valueOf(e.getId());
	}
}