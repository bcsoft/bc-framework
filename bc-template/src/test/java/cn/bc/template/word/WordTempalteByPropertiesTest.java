package cn.bc.template.word;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.apache.poi.POIXMLProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;
import org.openxmlformats.schemas.officeDocument.x2006.customProperties.CTProperties;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WordTempalteByPropertiesTest {
	// 加载docx文档
	private OPCPackage getPackage() throws IOException, InvalidFormatException {
		InputStream is = new ClassPathResource(
				"cn/bc/template/word/WordTplByProperties.docx")
				.getInputStream();
		OPCPackage p = OPCPackage.open(is);
		return p;
	}

	// 读取主文档部件
	@Test
	public void testRetrieveDocumentMainPart() throws Exception {
		OPCPackage p = getPackage();

		// Retrieve core part relationship from his type
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.CORE_DOCUMENT).getRelationship(0);

		// Get the content part from the relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml",
				part.getContentType());
		Assert.assertEquals("/word/document.xml", part.getPartName().getName());
	}

	// 获取文档属性
	@Test
	public void testGetDocumentCoreProperties() throws Exception {
		OPCPackage p = getPackage();

		// Get core properties part relationship
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);

		// Get core properties part from the previous relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-package.core-properties+xml",
				part.getContentType());
		Assert.assertEquals("/docProps/core.xml", part.getPartName().getName());

		PackageProperties pp = p.getPackageProperties();

		System.out.println("Creator=" + pp.getCreatorProperty().getValue()
				+ ",Title=" + pp.getTitleProperty().getValue() + ",Subject="
				+ pp.getSubjectProperty().getValue());
	}

	// 获取扩展属性：需要自己解析xml文档
	@Test
	public void testExtendedProperties() throws Exception {
		OPCPackage p = getPackage();

		// Get core properties part relationship
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.EXTENDED_PROPERTIES)
				.getRelationship(0);

		// Get extended properties part from the previous relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-officedocument.extended-properties+xml",
				part.getContentType());
		Assert.assertEquals("/docProps/app.xml", part.getPartName().getName());

		// Extract content
		InputStream inStream = part.getInputStream();

		// Create DOM parser
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder documentBuilder;
		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		// Parse XML content
		Document extPropsDoc = documentBuilder.parse(inStream);

		// Extract the name and the version of the Open XML file
		// generator
		System.out.println("Application="
				+ extPropsDoc.getElementsByTagName("Application").item(0)
						.getTextContent()
				+ ",AppVersion="
				+ extPropsDoc.getElementsByTagName("AppVersion").item(0)
						.getTextContent()
				+ "Words="
				+ extPropsDoc.getElementsByTagName("Words").item(0)
						.getTextContent()
				+ ",Characters="
				+ extPropsDoc.getElementsByTagName("Characters").item(0)
						.getTextContent()
				+ ",Lines="
				+ extPropsDoc.getElementsByTagName("Lines").item(0)
						.getTextContent());

		inStream.close();
	}

	// 获取自定义属性：需要自己解析xml文档
	@Test
	public void testCustomProperties() throws Exception {
		OPCPackage p = getPackage();

		// Get core properties part relationship
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.CUSTOM_PROPERTIES).getRelationship(0);

		if (relationship == null) {
			System.out.println("所测试的文档没有自定义属性");
			return;
		}

		// Get custom properties part from the previous relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-officedocument.custom-properties+xml",
				part.getContentType());
		Assert.assertEquals("/docProps/custom.xml", part.getPartName()
				.getName());

		// Extract content
		InputStream inStream = part.getInputStream();

		// Create DOM parser
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setIgnoringElementContentWhitespace(true);

		DocumentBuilder documentBuilder;
		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		// Parse XML content
		Document customPropsDoc = documentBuilder.parse(inStream);
		Node node;
		NodeList nodes = customPropsDoc.getElementsByTagName("property");
		// System.out.println(nodes.getLength());
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			System.out.println("name="
					+ node.getAttributes().getNamedItem("name").getNodeValue()
					+ ",value=" + node.getFirstChild().getTextContent());
		}

		inStream.close();
	}

	// 获取文档的字符串内容
	@Test
	public void testGetDocumentText() throws Exception {
		OPCPackage p = getPackage();
		XWPFWordExtractor docx = new XWPFWordExtractor(p);
		System.out.println(docx.getText());
	}

	// 创建文档
	@Test
	public void testCreateDocument() throws Exception {
		OPCPackage p = getPackage();

		// Get core properties part relationship
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);

		// Get core properties part from the previous relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-package.core-properties+xml",
				part.getContentType());
		Assert.assertEquals("/docProps/core.xml", part.getPartName().getName());

		PackageProperties pp = p.getPackageProperties();
		pp.setTitleProperty("新标题");// 修改标题

		// 生成新的文档
		XWPFDocument newDoc = new XWPFDocument(p);
		newDoc.write(new FileOutputStream("d:\\t\\newDoc.docx"));

		System.out.println("Creator=" + pp.getCreatorProperty().getValue()
				+ ",Title=" + pp.getTitleProperty().getValue() + ",Subject="
				+ pp.getSubjectProperty().getValue());
	}

	// 创建文档
	@Test
	public void testGetDocumentAllProperties() throws Exception {
		OPCPackage p = getPackage();

		// Get core properties part relationship
		PackageRelationship relationship = p.getRelationshipsByType(
				PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);

		// Get core properties part from the previous relationship
		PackagePart part = p.getPart(relationship);
		Assert.assertEquals(
				"application/vnd.openxmlformats-package.core-properties+xml",
				part.getContentType());
		Assert.assertEquals("/docProps/core.xml", part.getPartName().getName());

		XWPFDocument docx = new XWPFDocument(p);

		POIXMLProperties pp = docx.getProperties();
		CTProperties ctps = pp.getCustomProperties().getUnderlyingProperties();
		// System.out.println(ctps);
		// for (CTProperty ctp : ctps.getPropertyList()) {
		// System.out.println(ctp);
		// System.out.println(ctp.getLpwstr());
		// System.out.println(ctp.getI4());
		// }

		// 设置新的标题：ok
		pp.getCoreProperties().setTitle("新的标题属性值");

		// 设置新的自定义属性值：ok(需要在Word中配置"打印前更新域"<--选项/显示)
		// 如果使用后台转换为pdf打印可能存在域没有更新的情况
		ctps.getPropertyList().get(0).setLpwstr("新的自定义属性值");

		// 替换${name}占位符
		this.changeText(docx);

		// 写入到新文件
		docx.write(new FileOutputStream("/t/newDoc.docx"));
	}

	// 替换${name}占位符
	private void changeText(XWPFDocument document) {
		Iterator<XWPFParagraph> i = document.getParagraphsIterator();
		while (i.hasNext()) {
			XWPFParagraph paragraph = i.next();
			for (XWPFRun run : paragraph.getRuns()) {
				// System.out.println("========" + run.getCTR());
				for (CTText t : run.getCTR().getTList()) {
					if (t.getStringValue().indexOf("${name}") != -1) {
						// System.out.println("========" + t.getStringValue());
						t.setStringValue(t.getStringValue().replaceAll(
								"\\$\\{name\\}", "名称名称"));
					}
				}
			}
		}
	}

	// 创建文档
	@Test
	public void testReplaceMarker() throws Exception {
		OPCPackage p = getPackage();
		XWPFDocument docx = new XWPFDocument(p);

		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "1111");
		map.put("age", "30");

		// 替换占位符
		this.replaceMarker(docx, map);

		// 写入到新文件
		docx.write(new FileOutputStream("/t/newDoc.docx"));
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
		System.out.println("pattern=" + pattern);
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
						System.out.println("k=" + k + ",s="
								+ t.getStringValue());
						t.setStringValue(t.getStringValue().replaceAll(
								"\\$\\{" + k + "\\}", map.get(k)));
					}
				}
			}
		}
	}

	@Test
	public void testFindKeies() {
		String pattern = "(?<=\\$\\{)name(?=\\})|(?<=\\$\\{)编号(?=\\})";
		String source = "AA${name},${编号}BB,${编号}CC,name}";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		while (m.find()) {
			System.out.println(m.group());
		}
	}
}
