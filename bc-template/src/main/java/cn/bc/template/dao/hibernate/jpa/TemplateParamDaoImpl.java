package cn.bc.template.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;
import cn.bc.template.dao.TemplateParamDao;
import cn.bc.template.domain.TemplateParam;

/**
 * 模板参数DAO接口的实现
 * 
 * @author lbj
 * 
 */
public class TemplateParamDaoImpl extends HibernateCrudJpaDao<TemplateParam> implements
	TemplateParamDao {
	protected final Log logger = LogFactory.getLog(getClass());
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public Map<String,Object> getMap(String sql) throws Exception {
		try{
			
			return jdbcTemplate.queryForMap(sql);
			
		}catch (EmptyResultDataAccessException erde) {	
			logger.warn("sql="+sql+",查询无返回结果。");
			return null;
		}
	}

	public List<Object> getListIncludeObject(String sql) throws Exception {
		return jdbcTemplate.queryForList(sql, Object.class);
	}

	public List<Map<String, Object>> getListIncludeMap(String sql)
			throws Exception {
		return jdbcTemplate.queryForList(sql);
	}

	public List<List<Object>> getListIncludeList(String sql) throws Exception {
		return	HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), sql,null
		 	,new RowMapper<List<Object>>() {
				public List<Object> mapRow(Object[] rs, int rowNum) {
					List<Object> list = new ArrayList<Object>();
					for(Object o :rs){
						list.add(o);
					}
					return list;
				}
		});
	}
	
}
