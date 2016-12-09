package cn.bc.identity.web.rest;

import cn.bc.identity.domain.Duty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * 职务
 *
 * @author dragon 2016-07-21
 */
@Path("/duty")
public interface DutyResource {
	/**
	 * 获取职务列表信息
	 *
	 * @param pageNo   - 页码
	 * @param pageSize - 页容量
	 * @param search   - 模糊搜索的值
	 * @param format   - null 或 json 代表 json 格式, xlsx 代表为导出 Excel, 其余不支持
	 */
	@GET
	Response page(@QueryParam("pageNo") Integer pageNo,
	              @QueryParam("pageSize") Integer pageSize,
	              @QueryParam("search") String search,
	              @QueryParam("format") String format) throws IOException;

	/**
	 * 获取表单数据
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id: \\d+}")
	Duty get(@PathParam("id") Long id);

	/**
	 * 新建
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	String create(@FormParam("code") String code, @FormParam("name") String name);

	/**
	 * 更新
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id: \\d+}")
	String update(@PathParam("id") Long id, @FormParam("code") String code, @FormParam("name") String name);

	/**
	 * 删除 1 条
	 */
	@DELETE
	@Path("{id: \\d+}")
	void delete(@PathParam("id") Long id);

	/**
	 * 删除多条
	 *
	 * @param ids 多个ID间用逗号连接
	 */
	@DELETE
	@Path("{ids: .+}")
	void deleteBatch(@PathParam("ids") String ids);
}