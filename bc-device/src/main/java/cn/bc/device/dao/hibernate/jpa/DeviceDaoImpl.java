package cn.bc.device.dao.hibernate.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import cn.bc.device.dao.DeviceDao;
import cn.bc.device.domain.Device;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 设备Dao实现
 * 
 * 
 * @author hwx
 * 
 */
public class DeviceDaoImpl extends HibernateCrudJpaDao<Device> implements
		DeviceDao {
	private static Log logger = LogFactory.getLog(DeviceDaoImpl.class);
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Long checkDeviceCodeIsExist(String code) {
		// 查询所有状态的车辆
		String sql = "select id from bc_device where code=?";
		try {
			return this.jdbcTemplate.queryForLong(sql, new Object[] { code });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public String findDeviceCode(Long id) {
		String sql = "select code from bc_device where id=?";
		try {
			return jdbcTemplate.queryForObject(sql.toString(),
					new Object[] { id }, new RowMapper<String>() {
						public String mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getString(1);
						}
					});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
}
