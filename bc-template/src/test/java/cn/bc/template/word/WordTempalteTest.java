package cn.bc.template.word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;

public class WordTempalteTest {

	@Test
	public void testWriteWordFile() {
		String destFile = "D:\\t\\word01.doc";
		// #####################根据自定义内容导出Word文档#################################################
		StringBuffer fileCon = new StringBuffer();
		fileCon.append("               张大炮            男              317258963215223\n"
				+ "2011     09        2013     07       3\n"
				+ "    二炮研究              成人\n"
				+ "2013000001                             2013     07     08");
		fileCon.append("\n\r\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		exportDoc(destFile, fileCon.toString());
	}

	@Test
	public void testDealWordTplFile() {
		// ##################根据Word模板导出单个Word文档###################################################
		Map<String, String> map = new HashMap<String, String>();

		map.put("name", "Zues");
		map.put("sex", "男");
		map.put("idCard", "200010");
		map.put("year1", "2000");
		map.put("month1", "07");
		map.put("year2", "2008");
		map.put("month2", "07");
		map.put("gap", "2");
		map.put("zhuanye", "计算机科学与技术");
		map.put("type", "研究生");
		map.put("bianhao", "2011020301");
		map.put("nowy", "2011");
		map.put("nowm", "01");
		map.put("nowd", "20220301");
		// 注意biyezheng_moban.doc文档位置,此例中为应用根目录
		HWPFDocument document = replaceDoc("D:\\t\\wordTpl.doc", map);
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		try {
			document.write(ostream);
			// 输出word文件
			OutputStream outs = new FileOutputStream("D:\\t\\wordReal.doc");
			outs.write(ostream.toByteArray());
			outs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param destFile
	 * @param fileCon
	 */
	public static void exportDoc(String destFile, String fileCon) {
		try {
			// doc content
			ByteArrayInputStream bais = new ByteArrayInputStream(
					fileCon.getBytes());
			POIFSFileSystem fs = new POIFSFileSystem();
			DirectoryEntry directory = fs.getRoot();
			directory.createDocument("WordDocument", bais);
			FileOutputStream ostream = new FileOutputStream(destFile);
			fs.writeFilesystem(ostream);
			bais.close();
			ostream.close();

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
	public static HWPFDocument replaceDoc(String srcPath,
			Map<String, String> map) {
		try {
			// 读取word模板
			FileInputStream fis = new FileInputStream(new File(srcPath));
			HWPFDocument doc = new HWPFDocument(fis);
			// 读取word文本内容
			Range bodyRange = doc.getRange();
			System.out.println("----");
			System.out.println(bodyRange.text());
			// 替换文本内容
			for (Map.Entry<String, String> entry : map.entrySet()) {
				bodyRange.replaceText("${" + entry.getKey() + "}",
						entry.getValue());
			}
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
