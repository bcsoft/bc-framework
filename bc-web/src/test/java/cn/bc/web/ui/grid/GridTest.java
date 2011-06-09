package cn.bc.web.ui.grid;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.bc.core.Entity;
import cn.bc.test.Example;
import cn.bc.web.ui.html.grid.Grid;

public class GridTest {

	@Test
	public void test01() {
		Grid grid = buildTestGrid();
		grid.setBeautiful(true);

		//StringBuffer main = new StringBuffer();
		//System.out.println(grid.render(main));
	}

	public static Grid buildTestGrid() {
		List<Entity<Long>> data = new ArrayList<Entity<Long>>();
		Example e;
		for (int i = 0; i < 3; i++) {
			e = new Example();
			e.setId(new Long(i));
			e.setCode("code" + i);
			e.setName("name" + i);
			data.add(e);
		}

//		Grid grid = new Grid().addColumn(new IdColumn(true))
//				.addColumn(new TextColumn("code", "编码", 80))
//				.addColumn(new TextColumn("name", "名称")).setData(data);
//		return grid;
		return null;
	}
}
