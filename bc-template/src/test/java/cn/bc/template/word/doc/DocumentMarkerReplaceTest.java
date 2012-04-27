package cn.bc.template.word.doc;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class DocumentMarkerReplaceTest {
	// 加载xml文档的内容
	private HWPFDocument loadDocument() throws Exception {
		InputStream is = new ClassPathResource("cn/bc/template/word/docTpl.doc")
				.getInputStream();
		HWPFDocument document = new HWPFDocument(is);
		return document;
	}

	@Test
	public void testReplaceMarker() throws Exception {
		HWPFDocument doc = this.loadDocument();
		Map<String, String> map = new HashMap<String, String>();
		map.put("xingming", "小明");
		map.put("age", "30岁");

		// 读取word文本内容
		Range bodyRange = doc.getRange();
		System.out.println(bodyRange.text());
		// 替换文本内容
		for (Map.Entry<String, String> entry : map.entrySet()) {
			bodyRange
					.replaceText("${" + entry.getKey() + "}", entry.getValue());
		}
		bodyRange.insertAfter("新插入的文本");

		// 输出word文件
		OutputStream os = new FileOutputStream("/t/docTpl_marker.doc");
		doc.write(os);
		os.close();
	}
}
