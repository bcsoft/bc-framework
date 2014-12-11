package cn.bc.photo.service;

import cn.bc.identity.web.SystemContextHolder;
import cn.bc.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Attach 附件信息解析器
 *
 * @author dragon
 */
public class PhotoExecutor4Attach implements PhotoExecutor {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, Object> execute(String id) {
		String sql = "select '' as dir, path as path, subject as fname, format as format, size_ as size, ptype as ptype, puid as puid";
		sql += ", '" + SystemContextHolder.get().getContextPath() + "' || '/bc/attach/inline?id=' || id as url";
		sql += " from bc_docs_attach where id = ?";
		return this.jdbcTemplate.queryForMap(sql, new Long(id));
	}
}
