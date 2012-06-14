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
}
