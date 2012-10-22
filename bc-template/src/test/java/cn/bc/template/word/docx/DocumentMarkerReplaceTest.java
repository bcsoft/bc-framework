package cn.bc.template.word.docx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.springframework.core.io.ClassPathResource;

public class DocumentMarkerReplaceTest {
	// 加载docx文档
	private OPCPackage getPackage() throws IOException, InvalidFormatException {
		InputStream is = new ClassPathResource(
				"cn/bc/template/word/docxTpl.docx")
				.getInputStream();
		OPCPackage p = OPCPackage.open(is);
		return p;
	}

	// 替换文档中的标记占位符为实际的值
	@Test
	public void testReplaceMarker() throws Exception {
		OPCPackage p = getPackage();
		XWPFDocument docx = new XWPFDocument(p);

		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "1111");
		map.put("age", "301");
		map.put("comment", "comment\\r\\ncomment\n?comment\r\ncomment\rcomment\ncomment\tcomment<br>tcomment<br />tcomment");

		// 替换占位符
		this.replaceMarker(docx, map);

		// 写入到新文件
		docx.write(new FileOutputStream("/t/newDoc_marker.docx"));
	}

	private void replaceMarker(XWPFDocument document, Map<String, String> map) {
		Iterator<XWPFParagraph> ps = document.getParagraphsIterator();
		String pattern = "";
		int i = 0;
		for (String k : map.keySet()) {
			if (i > 0)
				pattern += "|";
			pattern += "(?<=\\$\\{)" + k + "(?=\\})";
			i++;
		}
		// System.out.println("pattern=" + pattern);
		Pattern p = Pattern.compile(pattern);
		Matcher m;
		String k;
		while (ps.hasNext()) {
			XWPFParagraph paragraph = ps.next();
			for (XWPFRun run : paragraph.getRuns()) {
				for (CTText t : run.getCTR().getTList()) {
					m = p.matcher(t.getStringValue());
					if (m.find()) {
						k = m.group();
						// System.out.println("k=" + k + ",s="
						// + t.getStringValue());
						t.setStringValue(t.getStringValue().replaceAll(
								"\\$\\{" + k + "\\}", map.get(k)));
					}
				}
			}
		}
	}

	// 获取匹配的键值
	@Test
	public void testFindKeies() {
		String pattern = "(?<=\\$\\{)name(?=\\})|(?<=\\$\\{)编号(?=\\})";
		String source = "AA${name},${编号}BB,${编号}CC,name}";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		m.find();
		Assert.assertEquals("name", m.group());
		m.find();
		Assert.assertEquals("编号", m.group());
		m.find();
		Assert.assertEquals("编号", m.group());
		// while (m.find()) {
		// System.out.println(m.group());
		// }
	}
}
