package cn.bc.template.word.docx;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class DocumentTextTest {
	// 加载docx文档
	private OPCPackage getPackage() throws IOException, InvalidFormatException {
		InputStream is = new ClassPathResource(
				"cn/bc/template/word/docxTpl.docx")
				.getInputStream();
		OPCPackage p = OPCPackage.open(is);
		return p;
	}
	// 获取文档的字符串内容
	@Test
	public void testGetDocumentText() throws Exception {
		OPCPackage p = getPackage();
		XWPFWordExtractor docx = new XWPFWordExtractor(p);
		Assert.assertNotNull(docx.getText());
//		System.out.println(docx.getText());
	}
}
