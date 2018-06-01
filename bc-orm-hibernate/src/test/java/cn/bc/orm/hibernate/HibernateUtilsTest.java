package cn.bc.orm.hibernate;

import org.junit.Assert;
import org.junit.Test;

public class HibernateUtilsTest {
  @Test
  public void removeSelect_simple() {
    String sql1 = "select a,b";
    String sql2 = "from A2 where a=1 order by a asc";
    String sql = sql1 + " " + sql2;
    Assert.assertEquals(sql2, HibernateUtils.removeSelect(sql));
  }

  @Test
  public void removeSelect_childselect() {
    String sql1 = "select a,b,(select c,d from A1 where c=1 order by c desc),d";
    String sql2 = "from A2 where a=1 order by a asc";
    String sql = sql1 + " " + sql2;
    Assert.assertEquals(sql2, HibernateUtils.removeSelect(sql));
  }
//
//	@Test
//	public void removeSelect_tt() {
//		String sql1 = "select a,b,(select c,d from A1 where c=1 order by c desc),d";
//		String sql2 = "from A2 where a=(select c1,d1 from A11 where c1=1 order by c1 desc) order by a asc";
//		String sql = sql1 + " " + sql2;
//		String[] ss = sql.split("^select .* from");
//		System.out.println(ss.length);
//		for (String s : ss) {
//			System.out.println("-" + s + "-");
//		}
//	}
}
