package cn.bc.photo.service;

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
        String sql = "select ext as type, path_ as path, subject as name from bc_wf_attach where id = ?";
        return this.jdbcTemplate.queryForMap(sql, new Long(id));
    }
}
