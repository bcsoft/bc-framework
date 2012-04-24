package cn.bc.template.word.xml2003;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import cn.bc.core.util.TemplateUtils;

public class DocumentMarkerReplaceTest {
	// 加载xml文档的内容
	private String loadXmlContent() throws Exception {
		InputStream is = new ClassPathResource(
				"cn/bc/template/word/xml2003Tpl.xml").getInputStream();
		InputStreamReader reader = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		String s = "";
		String str = null;
		while ((str = br.readLine()) != null) {
			s += str;
		}
		return s;
	}

	@Test
	public void testReplaceMarker() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("xingming", "小明");
		map.put("age", "30岁");
		String content = this.loadXmlContent();
		Assert.assertNotNull(content);
		Assert.assertTrue(content.length() > 0);
		content = TemplateUtils.format(content, map);
//		content = FreeMarkerUtils.format(content, map);
		// 输出word文件
		OutputStream outs = new FileOutputStream("/t/xml2003_marker.xml");
		outs.write(content.getBytes());
		outs.close();
		
		//Assert.assertEquals(content, this.loadXmlContent());
	}
}
