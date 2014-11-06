/**
 * 
 */
package cn.bc.core.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用正则表达式切割sql语句的测试
 * 
 * @author dragon
 * 
 */
public class RegExp4SqlTest {
	private String removeOrderByRegex = "order\\s*by[\\w|\\W|\\s|\\S]*";

	private String removeOrderBy(String sql) {
		return removeOrderBy(sql, removeOrderByRegex);
	}

	private String removeOrderBy(String sql, String regex) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	@Test
	public void testRemoveOrderBy00() throws Exception {
		String sql = "select id,name,(select id from A a order by id) from T t where id=? order by file_date desc";
		Pattern p = Pattern.compile(".*[(].*[)].*order\\s*by[\\w|\\W|\\s|\\S]*");
		Matcher m = p.matcher(sql);
		String val = null;
		while (m.find()) {
			val = m.group();
			System.out.println("MATCH:" + val);
		}
		if (val == null) {
			System.out.println("NO MATCHES");
		}
	}

	@Test
	public void testRemoveOrderBy01() throws Exception {
		String sql = "select t.id,t.name from T t where t.id=? order by t.file_date desc";
		String t = removeOrderBy(sql);
		Assert.assertEquals("select t.id,t.name from T t where t.id=? ", t);
	}

	@Test
	public void testRemoveOrderBy02() throws Exception {
		String sql = "select t.id,t.name from T t\r\n where t.id=?\r\n order by t.file_date desc";
		String t = removeOrderBy(sql);
		Assert.assertEquals(
				"select t.id,t.name from T t\r\n where t.id=?\r\n ", t);
	}

	@Test
	public void testRemoveOrderBy03() throws Exception {
		String sql = "select t.id,t.name,(select id from A a order by a.id) from T t where t.id=? order by t.file_date desc";
		String t = removeOrderBy(sql, "(order\\s*by[\\w|\\W|\\s|\\S]*)?");
		System.out.println("t=" + t);
		Assert.assertEquals(
				"select t.id,t.name,(select id from A a order by a.id) from T t where t.id=? ",
				t);
	}
}
