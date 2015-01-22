package cn.bc.workday.dao.hibernate.jpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.workday.dao.WorkdayDao;
import cn.bc.workday.domain.Workday;

/**
 * 
 * @author LeeDane
 *
 */
public class WorkdayDaoImpl extends HibernateCrudJpaDao<Workday> implements WorkdayDao{
	private static Log logger = LogFactory.getLog(WorkdayDaoImpl.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public boolean checkDateIsCross(Date fromDate, Date toDate) {
		
		/**
		*思路：(返回的是交叉的个数)
		*1、判断from_date是否已经存在的数据库中的任意时间段内
		*2、判断to_date是否已经存在的数据库中的任意时间段内
		*3、判断from_date和to_date之间是否已经在数据库中
		*	3.1、根据from_date找日期比其大的第一条记录from_date为：after_from_fate
		*	3.2、判断after_from_date是否存在，存在则将after_from_date和to_date进行比较，
		*/
		String sql = "with after_from_date(after_from_date) as( "
					+ " select w1.from_date from bc_workday w1 where ?::date < w1.from_date "
					+ " and not exists( "
					+ " select 0 from bc_workday w11 "
					+ " where ?::date < w11.from_date "
					+ " and w1.from_date > w11.from_date "
					+ " ) "
				+ ")"
				+ " select count(*) ct "
				+ " from bc_workday w "
				+ " where ( ? ::date between w.from_date and w.to_date) "
				+ " or (?::date between w.from_date and w.to_date) "
				+ " or ( "
					+ " (select * from after_from_date afd) is not null "
					+ " and "
					+ " (select * from after_from_date afd) < ? ::date "
				+ " ) ";
		if(logger.isDebugEnabled())
			logger.debug("fromDate: "+ fromDate + ", toDate" + toDate);
		
		int count = jdbcTemplate.queryForObject(sql, new Object[]{fromDate, fromDate, fromDate, toDate, toDate}, new RowMapper<Integer>(){

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {				
				return rs.getInt("ct");
			}});
		return count > 0 ? true : false;
	}
	@Override
	public boolean checkDateIsCross(long id, Date fromDate, Date toDate) {
		/**
		*思路：(返回的是交叉的个数)
		*1、判断from_date是否已经存在的数据库中的任意时间段内
		*2、判断to_date是否已经存在的数据库中的任意时间段内
		*3、判断from_date和to_date之间是否已经在数据库中
		*	3.1、根据from_date找日期比其大的第一条记录from_date为：after_from_fate
		*	3.2、判断after_from_date是否存在，存在则将after_from_date和to_date进行比较，
		*/
		String sql = "with after_from_date(after_from_date) as( "
					+ " select w1.from_date from bc_workday w1 where ?::date < w1.from_date "
					+ " and not exists( "
						+ " select 0 from bc_workday w11 "
						+ " where ?::date < w11.from_date "
						+ " and w1.from_date > w11.from_date "
						+ " and w11.id <> ?"
					+ " ) "
					+ " and w1.id <> ?"
				+ ")"
				+ " select count(*) ct "
				+ " from bc_workday w "
				+ " where w.id <> ? "
				+ " and ( "
					+ " ( ? ::date between w.from_date and w.to_date) "
					+ " or (?::date between w.from_date and w.to_date) "
					+ " or ( "
						+ " (select * from after_from_date afd) is not null "
						+ " and "
						+ " (select * from after_from_date afd) < ? ::date "
					+ " ) "
				+ " ) ";
		if(logger.isDebugEnabled())
			logger.debug("fromDate: "+ fromDate + ", toDate" + toDate);
		
		int count = jdbcTemplate.queryForObject(sql, new Object[]{fromDate, fromDate, id, id, id, fromDate, toDate, toDate}, new RowMapper<Integer>(){

			@Override
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {				
				return rs.getInt("ct");
			}});
		return count > 0 ? true : false;
	}

	
}
