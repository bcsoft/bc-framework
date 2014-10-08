package cn.bc.db.jdbc.spring;

import cn.bc.core.Page;
import cn.bc.core.query.Query;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional("txManager")
@ContextConfiguration("classpath:spring-test.xml")
public class SpringJdbcPostgresTypeTest {
	private JdbcTemplate jdbcTemplate;
	private DataSource dataSource;

	protected String getTable() {
		return "BC_EXAMPLE";
	};

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	public void testQueryArrayType() throws Exception {
		String sql = "select id, code, array_int, json_, row_to_json(t) row_to_json from bc_example t";
		List<Map<String,Object>> all = this.jdbcTemplate.queryForList(sql);
		System.out.println(StringUtils.collectionToCommaDelimitedString(all));
		for(Map<String,Object> m : all){
			for(Map.Entry<String,Object> e : m.entrySet()){
				System.out.println(e.getKey() + "=" + e.getValue() + "|" + (e.getValue() != null ? e.getValue().getClass() : "null"));
			}
		}

		// 数组类型数据的获取 http://www.cnblogs.com/tnjin/p/3161735.html
		java.sql.Array array = (Array) all.get(0).get("array_int"); // org.postgresql.jdbc4.Jdbc4Array
		Integer[] ints = (Integer[])array.getArray();//通过getArray方法，会返还一个Object对象，将obj强转为最终类型的数组
		System.out.println("array=" + ints);

		// json类型数据的获取
		org.postgresql.util.PGobject j = (org.postgresql.util.PGobject) all.get(0).get("json_");;
		System.out.println("json=" + j.getValue());// 获取json的字符串值
		j = (org.postgresql.util.PGobject) all.get(0).get("row_to_json");;
		System.out.println("row_to_json=" + j.getValue());// 获取json的字符串值
	}

	@Test
	public void testQueryJsonArray() throws Exception {
		String sql = "select array_agg(row_to_json(t)) row_to_jsons from bc_example t";
		List<Map<String,Object>> all = this.jdbcTemplate.queryForList(sql);
		System.out.println(StringUtils.collectionToCommaDelimitedString(all));
		for(Map<String,Object> m : all){
			for(Map.Entry<String,Object> e : m.entrySet()){
				System.out.println(e.getKey() + "=" + e.getValue() + "|" + (e.getValue() != null ? e.getValue().getClass() : "null"));
			}
		}

		org.postgresql.jdbc4.Jdbc4Array a;
		// json数组类型数据的获取 http://www.cnblogs.com/tnjin/p/3161735.html
		java.sql.Array array = (Array) all.get(0).get("row_to_jsons"); // org.postgresql.jdbc4.Jdbc4Array
		System.out.println(array.getArray());
		System.out.println(array.getArray().getClass());
		PGobject[] js = (PGobject[])array.getArray();//通过getArray方法，会返还一个Object对象，将obj强转为最终类型的数组
		System.out.println("row_to_jsons=" + js);
	}

	private Query<Domain> createTestQuery() {
		Query<Domain> q = new SpringJdbcQuery<Domain>(dataSource,
				new RowMapper<Domain>() {
					public Domain mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Domain t = new Domain();
						t.setId(rs.getLong("id"));
						return t;
					}
				}).setSql("select * from " + getTable() + " order by id");
		return q;
	}

	/**
	 * 删除所有数据，用于清空测试现场
	 */
	protected void deleteAll() {
		this.jdbcTemplate.execute("delete from " + this.getTable());
	}

	protected Long insertOne(int id) {
		this.jdbcTemplate.execute("insert into " + this.getTable()
				+ "(id,code,name) values(" + id + ",'code','name')");
		return new Long(id);
	}

	public class Domain {
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	}
}
