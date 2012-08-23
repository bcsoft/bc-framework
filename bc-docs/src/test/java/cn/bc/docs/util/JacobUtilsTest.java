package cn.bc.docs.util;

import org.junit.Test;

public class JacobUtilsTest {
	@Test
	public void word2pdf() throws Exception {
		// 需要将dll方法到jdk的bin目录下
		String inputFile = "D:\\t\\补充协议FJ07A04.docx";
		String pdfFile = "D:\\t\\补充协议FJ07A04.pdf";
		JacobUtils.word2PDF(inputFile, pdfFile);
	}
}
