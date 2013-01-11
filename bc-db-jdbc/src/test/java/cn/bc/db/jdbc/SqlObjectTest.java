package cn.bc.db.jdbc;

import junit.framework.Assert;

import org.junit.Test;

public class SqlObjectTest {
	@Test
	public void testRemoveOrderBy01() {
		String sql = "select a,b from c where a=1 order by a asc";
		Assert.assertEquals("select a,b from c where a=1 ",
				SqlObject.removeOrderBy(sql));
	}

	@Test
	public void testRemoveOrderBy04() {
		String sql = "select a,(select a,b from d where b=1 order by b) from c where a=1 order by a asc";
		Assert.assertEquals("select a,(select a,b from d where b=1 ",
				SqlObject.removeOrderBy(sql));
	}

	@Test
	public void testRemoveOrderBy02() {
		String sql = "select a,b from c where a=1 $$order by a asc";
		Assert.assertEquals("select a,b from c where a=1 ",
				SqlObject.removeOrderBy(sql));
	}

	@Test
	public void testRemoveOrderBy03() {
		String sql = "select a,(select a,b from d where b=1 order by b) from c where a=1 $$order by a asc";
		Assert.assertEquals(
				"select a,(select a,b from d where b=1 order by b) from c where a=1 ",
				SqlObject.removeOrderBy(sql));
	}

	@Test
	public void testRemoveOrderBy05() {
		String sql = "select a,(select a,b from d where b=1 order by b) $$from c where a=1 $$order by a asc";
		SqlObject<Object[]> s = new SqlObject<Object[]>();
		s.setSql(sql);
		Assert.assertEquals(" c where a=1 ", s.getFromWhereSql());
	}

	@Test
	public void testReplace$$() {
		String sql = "$$order";
		Assert.assertEquals("order", sql.replace("$$", ""));
	}

	@Test
	public void testIgnoreReplace() {
		String right = "select * from AA where B=1 and a=true order by C";
		String sql = "SELECT * FROM AA WHERE B=1 and a=true ORDER BY C";
		Assert.assertEquals(right, innerDealSql(sql));

		sql = "Select * frOm AA whEre B=1 and a=true orDer bY C";
		Assert.assertEquals(right, innerDealSql(sql));
	}

	/**
	 * 将sql中的关键字select、from、where和order by不区分大小写转换为小写
	 * 
	 * @param sql
	 * @return
	 */
	private String innerDealSql(String sql) {
		if (sql == null)
			return sql;
		return sql.replaceAll("(?i)select", "select")
				.replaceAll("(?i)from", "from")
				.replaceAll("(?i)where", "where")
				.replaceAll("(?i)order by", "order by");
	}
}
