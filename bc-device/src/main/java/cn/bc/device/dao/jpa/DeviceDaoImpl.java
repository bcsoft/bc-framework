package cn.bc.device.dao.jpa;

import cn.bc.device.dao.DeviceDao;
import cn.bc.device.domain.Device;
import cn.bc.orm.jpa.JpaCrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 设备Dao实现
 *
 * @author hwx
 */
public class DeviceDaoImpl extends JpaCrudDao<Device> implements DeviceDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Long checkDeviceCodeIsExist(String code) {
		// 查询所有状态的设备
		String sql = "select id from bc_device where code=?";
		try {
			return this.jdbcTemplate.queryForObject(sql, new Object[]{code}, Long.class);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public String findDeviceCode(Long id) {
		String sql = "select code from bc_device where id=?";
		try {
			return jdbcTemplate.queryForObject(sql.toString(), new Object[]{id}, new RowMapper<String>() {
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					return rs.getString(1);
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}