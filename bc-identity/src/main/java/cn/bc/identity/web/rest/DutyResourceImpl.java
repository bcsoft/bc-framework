package cn.bc.identity.web.rest;

import cn.bc.core.Page;
import cn.bc.core.exception.NotExistsException;
import cn.bc.core.query.condition.AdvanceCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import cn.bc.rest.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.HashMap;
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
	public Response page(Integer pageNo, Integer pageSize, String search, String format) throws IOException {
		// 获取数据
		Page<Duty> page = service.page(pageNo, pageSize, AdvanceCondition.toConditions(search));
		Map<String, Object> data = new HashMap<>();
		data.put("rows", page.getData());
		data.put("count", page.getTotalCount());

		if (format == null || "json".equals(format)) {   // 返回 json
			return RestUtils.responseJson(data);
		} else if ("xlsx".equals(format)) {              // 导出 Excel
			return RestUtils.responseExcel(data, "cn/bc/identity/template/DutyView.xlsx", "职务.xlsx", false);
		} else {
			throw new UnsupportedOperationException("不支持的格式：format=" + format);
		}
	}

	@Override
	public Duty get(Long id) {
		Duty e = service.load(id);
		if (e == null) throw new NotExistsException("所要获取的职务信息不存在！");
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