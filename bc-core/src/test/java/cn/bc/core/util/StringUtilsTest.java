/**
 * 
 */
package cn.bc.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author dragon
 * 
 */
public class StringUtilsTest {
	@Test
	public void countMatches() {
		Assert.assertEquals(1, StringUtils.countMatches("?", "?"));
		Assert.assertEquals(1, StringUtils.countMatches("c.a=?", "?"));
		Assert.assertEquals(2, StringUtils.countMatches("?,?", "?"));
		Assert.assertEquals(2, StringUtils.countMatches("c.a in (?,?)", "?"));
		Assert.assertEquals(2,
				StringUtils.countMatches("c.a>=? or c.bb >=?", "?"));
	}

	@Test
	public void wenhaoReplace() {
		Assert.assertEquals("a1>? and a2<?",
				"a1>?0 and a2<?1".replaceAll("\\?\\d+", "\\?"));
		Assert.assertEquals("a1>? and a2<?",
				"a1>?0 and a2<?11".replaceAll("\\?\\d+", "\\?"));
		Assert.assertEquals("a1 in (?,?)",
				"a1 in (?0,?1)".replaceAll("\\?\\d+", "\\?"));
	}

	@Test
	public void wenhaoMatch() {
		String candidate = "a1>? and a2<?71 and a2<?0";
		String regex = "\\?\\d+";// 匹配?[num]模式
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(candidate);
		String val = null;
		while (m.find()) {
			val = m.group();
			System.out.println("MATCH: " + val);
		}
		if (val == null) {
			System.out.println("NO MATCHES: ");
		}
	}

	@Test
	public void t() {
		// String s =
		// "select cl.id cid,car.company\r\n\tfrom BS_CONTRACT_LABOUR cl";
		String s = "select cl.id cid,car.company,u.name unitName,m.name mName,car.plate_type||'.'||car.plate_no as plate,car.code carCode,0\r\n\t,man.name manName,";
		s += "cl.insurcode,man.cert_identity,cl.house_type,c.sign_date,c.start_date,c.end_date\r\n\t,cl.joindate,cl.insurance_type,man.phone,0,car.bs_type,car.register_date,c.file_date cfile_date \r\n\tfrom BS_CONTRACT_LABOUR cl\r\n\tinner join BS_CONTRACT c on c.id = cl.id\r\n\tinner join BS_CARMAN_CONTRACT manc on manc.contract_id = c.id\r\n\tinner join BS_CARMAN man on man.id = manc.man_id\r\n\tinner join BS_CAR_CONTRACT carc on carc.contract_id = c.id\r\n\tinner join BS_CAR car on car.id = carc.car_id\r\n\tinner join bs_motorcade m on m.id = car.motorcade_id \r\n\tinner join bc_identity_actor u on u.id=m.unit_id \r\n\twhere c.status_ = 0 \r\n\t";
		String regex = "^select .*\\s+.*from ";// 匹配?[num]模式
		String[] ss = s.split(regex);
		System.out.println(ss.length);
		// Assert.assertEquals(2, ss.length);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);
		String val = null;
		while (m.find()) {
			val = m.group();
			System.out.println("MATCH: " + val);
		}
		if (val == null) {
			System.out.println("NO MATCHES: ");
		}
	}

	@Test
	public void compressHtml() throws Exception {
		StringBuffer html = new StringBuffer();
		html.append("<a>");
		html.append("	<span>test</span>\r\n");
		html.append("</a><br/>");
		System.out.println(StringUtils.compressHtml(html.toString()));
	}
}
