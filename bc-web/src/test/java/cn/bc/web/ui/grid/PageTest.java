package cn.bc.web.ui.grid;

import org.junit.Test;

import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.ListPage;
import cn.bc.web.ui.html.page.PageOption;

public class PageTest {
	@Test
	public void testListPage() {
		ListPage listPage = new ListPage();
		listPage.setGrid(GridTest.buildTestGrid())
				.setCreateUrl("/duty/create")
				.setDeleteUrl("/duty/delete")
				.setEditUrl("/duty/edit")
				.addJs("js1").addCss("css1").setInitMethod("iniMethod")
				.setOption(
						new PageOption()
								.setButtons(
										new ButtonOption[] {
												new ButtonOption("ok", "delete"),
												new ButtonOption("edit", "edit"),
												new ButtonOption("create", "create") })
								.setMinWidth(200).setMinHeight(250)
								.setWidth(500).setHeight(400).setModal(false)
								.toString()).setBeautiful(true);

		StringBuffer main = new StringBuffer();
		System.out.println(listPage.render(main));
	}
}
