package cn.bc.photo.service;

import cn.bc.identity.web.SystemContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * 流程附件信息解析器
 *
 * @author dragon
 */
public class PhotoExecutor4FlowAttach implements PhotoExecutor {
  private JdbcTemplate jdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> execute(String id) {
    String sql = "select 'workflow/attachment' as dir, path_ as path, subject as fname, ext as format, size_ as size";
    sql += ", '" + SystemContextHolder.get().getContextPath() + "' || '/bc-workflow/flowattachfile/inline?id=' || id as url";
    sql += " from bc_wf_attach where id = ?";
    return this.jdbcTemplate.queryForMap(sql, new Long(id));
  }
}
