package cn.bc.identity.web.rest;

import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import com.owlike.genson.Genson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * 职务
 *
 * @author dragon 2016-07-14
 */
@Singleton
@Path("duty")
public class DutyResource {
	private final static Logger logger = LoggerFactory.getLogger(DutyResource.class);

	public DutyResource() {
		logger.debug("this={}", this);
	}

	@PostConstruct
	private void init() {
		logger.debug("init: service={}", service);
	}

	@Inject
	private DutyService service;

	// 获取视图数据
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	public String data(@QueryParam("pageNo") int pageNo,
	                   @QueryParam("pageSize") int pageSize,
	                   @QueryParam("searchText") String search) {
		Map<String, Object> data = service.data(pageNo, pageSize, search);
		return new Genson().serialize(data);
	}

	// 获取表单数据
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id: \\d+}")
	public Duty get(@PathParam("id") Long id) {
		if (id == 0) throw new WebApplicationException();
		return service.load(id);
	}

	// 新建
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public String create(@FormParam("code") String code, @FormParam("name") String name) {
		String id = saveOrUpdate(null, code, name);
		return "{\"id\": " + id + "}";
	}

	// 更新
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id: \\d+}")
	public String update(@PathParam("id") Long id, @FormParam("code") String code, @FormParam("name") String name) {
		if (id == 1123) { // TODO 封装到全局异常处理，不需要每个类自行处理
			throw new WebApplicationException(Response.status(Response.Status.SEE_OTHER)
					.type(MediaType.TEXT_HTML)
					.entity("111 aaa 异常测试") // 把异常信息放在响应的 body 中
					.build());
		}
		saveOrUpdate(id, code, name);
		return "{\"id\": " + id + "}";
	}

	private String saveOrUpdate(Long id, String code, String name) {
		logger.debug("id={}, code={}, name={}", id, code, name);
		Duty e;
		if (id != null) {
			e = service.load(id);
			if (e == null)
				throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
						.type(MediaType.TEXT_HTML)
						.entity("所要更新的职务信息不存在！")
						.build());
		} else {
			e = new Duty();
		}
		e.setCode(code);
		e.setName(name);
		service.save(e);
		return String.valueOf(e.getId());
	}

	// 删除
	@DELETE
	@Path("{ids: \\d+|[0-9,]+}")    // id1[,id2]
	public Response delete(@PathParam("ids") String ids) {
		service.delete(StringUtils.stringArray2LongArray(ids.split(",")));
		return Response.noContent().build();
	}
}