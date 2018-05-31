/**
 * 
 */
package cn.bc.core.util;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

;

/**
 * 
 * @author dragon
 * 
 */
@Ignore
public class TemplateUtilsTest {
	@Test
	public void testFormatByMapParams() {
		String tpl = "${k1}-${k2}";
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("k1", "v1");
		args.put("k2", 2);
		Assert.assertEquals("v1-2", TemplateUtils.format(tpl, args));
	}

	@Test
	public void testFormatByArrayParams() {
		String tpl = "{0}-{1}";
		Object[] args = new Object[] { "v1", 2 };
		Assert.assertEquals("v1-2", TemplateUtils.format(tpl, args));
	}
	
	@Test
	public void testFormatByFindMarkers() {
		String source = "";
		
		source += "${t02MotorcadeLeaderCheck.assignee.b.你好!'　　　　　'}";
		source += "${t03MotorcadeLeaderCheck.assignee?string('','')?string('','')}";
		//String source = "${t02MotorcadeLeaderCheck.assignee}";
		//String source = "${t02MotorcadeLeaderCheck}";
		//String source = "${t02MotorcadeLeaderCheck!}";
		
		List<String> markers = new ArrayList<String>();
		//String pattern = "(?<=\\$\\{)(\\w+\\.?\\w*)(?=\\'|\\!\\'\\s*\\'\\}|\\?|\\})";
		String pattern = "(?<=\\$\\{)([\\w\u4e00-\u9fa5]+(\\.?[\\w\u4e00-\u9fa5]*)*)(?=\\!|\\?|\\})";
		//String pattern = "(?<=\\$\\{)(\\w+)(?=\\}|\\!)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		String key;
		
		System.out.println("source="+source);
		
		while (m.find()) {
			key = m.group();
			if (!markers.contains(key)) {
				markers.add(key);
			}
		}
		
		System.out.println(markers.size());
		
		for(String marker:markers){
			System.out.println(marker);
		}
	}
	
	@Test
	public void testFormatByFindMarkers2() {
		String source = "";
		
		source += "转蓝<#if vs.logoutReason??&&vs.logoutReason==\"转蓝\">√<#else>□</#if>报废<#if vs.logoutReason??&&vs.logoutReason==\"报废\">√<#else>□</#if>其它<#if vs.logoutReason??&&vs.logoutReason==\"其它\">√<#else>□</#if>";
		source += "${t03MotorcadeLeaderCheck.assignee?string('','')?string('','')}";
		source += "${t02MotorcadeLeaderCheck.assignee}";
		source += "${t04MotorcadeLeaderCheck}";
		source += "${t05MotorcadeLeaderCheck!}";

		
		List<String> markers = new ArrayList<String>();
		//String pattern = "(?<=\\$\\{)(\\w+\\.?\\w*)(?=\\'|\\!\\'\\s*\\'\\}|\\?|\\})";
		String pattern = "(?<=\\$\\{)([\\w\u4e00-\u9fa5]+(\\.?[\\w\u4e00-\u9fa5]*)*)(?=\\!|\\?|\\})|(?<=\\<\\#if\\s)([\\w\u4e00-\u9fa5]+(\\.?[\\w\u4e00-\u9fa5]*)*)(?=\\?\\?|\\=\\=|\\>)";
		//String pattern = "(?<=\\$\\{)(\\w+)(?=\\}|\\!)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		String key;
		
		System.out.println("source="+source);
		
		while (m.find()) {
			key = m.group();
			if (!markers.contains(key)) {
				markers.add(key);
			}
		}
		
		System.out.println(markers.size());
		
		for(String marker:markers){
			System.out.println(marker);
		}
	}
}
