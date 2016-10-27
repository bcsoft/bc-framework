package cn.bc.identity.web.rest;

import cn.bc.identity.domain.Duty;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 职务
 *
 * @author dragon 2016-07-21
 */
@Path("duty")
public interface DutyResource {
	/**
	 * 获取视图数据
	 */
	@GET
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_JSON)
	String data(@QueryParam("pageNo") int pageNo,
	            @QueryParam("pageSize") int pageSize,
	            @QueryParam("searchText") String search);

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