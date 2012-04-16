package cn.bc.template.word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.junit.Test;

public class XmlWordTplTest {

	@Test
	public void test01() {
		String s = "<w:t>name=${name}</w:t>";
		Assert.assertEquals("<w:t>name=dragon</w:t>",
				s.replaceAll("\\$\\{name\\}", "dragon"));
	}

	@Test
	public void testDealWordTplFile() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "小名");
		map.put("age", "2011-01-01");
		String document = replaceDoc("D:\\t\\Word模板测试.xml", map);
		try {
			// 输出word文件
			OutputStream outs = new FileOutputStream("D:\\t\\Word模板测试Real.xml");
			outs.write(document.getBytes());
			outs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取word模板并替换变量
	 * 
	 * @param srcPath
	 * @param map
	 * @return
	 */
	public static String replaceDoc(String srcPath, Map<String, String> map) {
		try {
			// 读取word模板
			// ZipFile docxFile = new ZipFile(new File(srcPath));
			// ZipEntry documentXML = docxFile.getEntry("word/document.xml");
			// InputStream documentXMLIS = docxFile.getInputStream(documentXML);
			String s = "";

			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(srcPath), "UTF-8");
			BufferedReader br = new BufferedReader(reader);
			String str = null;

			while ((str = br.readLine()) != null) {
				s = s + str;
			}
			System.out.println(s);
			// 替换文本内容
			for (Map.Entry<String, String> entry : map.entrySet()) {
				s = s.replaceAll("\\$\\{" + entry.getKey() + "\\}",
						entry.getValue());
			}
			reader.close();
			br.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
