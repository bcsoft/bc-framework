package cn.bc.template.word.docx;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipTypes;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentPropertiesTest {
	// 加载docx文档
	private OPCPackage getPackage() throws IOException, InvalidFormatException {
		InputStream is = new ClassPathResource(
				"cn/bc/template/word/docxTpl.docx")
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
		newDoc.write(new FileOutputStream("/t/newDoc_properties.docx"));

		System.out.println("Creator=" + pp.getCreatorProperty().getValue()
				+ ",Title=" + pp.getTitleProperty().getValue() + ",Subject="
				+ pp.getSubjectProperty().getValue());
	}
}
