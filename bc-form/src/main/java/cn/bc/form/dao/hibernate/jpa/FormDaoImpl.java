package cn.bc.form.dao.hibernate.jpa;

import cn.bc.form.dao.FormDao;
import cn.bc.form.domain.Form;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义表单DaoImpl
 *
 * @author hwx
 */

public class FormDaoImpl extends HibernateCrudJpaDao<Form> implements FormDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public void delete(String type, String code, Long pid, Float ver) {
		String sql = "delete from bc_form where type_=? and code=? and pid=? and ver_=?";
		this.executeSql(sql, new Object[]{type, code, pid, ver});
	}

	public Float getNewestVer(String type, String code, Long pid) {
		String sql = "SELECT ver_ FROM bc_form where type_ = ? and code = ? and pid = ? order by ver_ desc limit 1";
		Float ver = this.jdbcTemplate.queryForObject(sql, new Object[]{type, code, pid}, new RowMapper<Float>() {
			public Float mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getFloat(1);
			}
		});
		return ver;
	}
}
