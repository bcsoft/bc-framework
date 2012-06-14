package cn.bc.docs.web.ui.html;

import org.junit.Test;

import cn.bc.docs.domain.Attach;

public class AttachWidgetTest {
	@Test
	public void test01() {
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setBeautiful(true);
		System.out.println(attachsUI.toString());

		Attach attach = new Attach();
		attach.setSize(1024);
		attach.setSubject("测试文档.doc");
		attach.setPuid("puid");
		attach.setPtype("ptype");
		attach.setFormat("doc");
		attach.setId(new Long(1));
		attach.setPath("/bc/test/test.doc");
		attachsUI.addAttach(attach);
		System.out.println(attachsUI.toString());
	}
//
//	@Test
//	public void test02() {
//		String path = "images/2011/08/test.jpg";
//		path = path.substring(0, path.lastIndexOf("/") + 1);
//		System.out.println(path);
//	}
}
