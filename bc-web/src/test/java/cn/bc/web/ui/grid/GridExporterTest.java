package cn.bc.web.ui.grid;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jxls.transformer.XLSTransformer;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import cn.bc.core.util.DateUtils;

public class GridExporterTest {
	/**
	 * 测试使用jxls基于excel模板文件导出excel文件
	 * 
	 * @throws Exception
	 */
	@Test
	public void test01() throws Exception {
		Map<String, Object> map = buildData();
		XLSTransformer transformer = new XLSTransformer();
		// 使用默认的模板
		InputStream is = new ClassPathResource("cn/bc/web/template/export.xls")
				.getInputStream();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);
		transformer.transformXLS(is, map).write(outputStream);
		is.close();
		outputStream.flush();
		outputStream.close();
	}

	private Map<String, Object> buildData() {
		Map<String, Object> map = new HashMap<String, Object>();

		// 导出时间
		map.put("exportTime", DateUtils.formatDateTime(new Date()));

		// 标题
		map.put("title", "测试标题");

		// 表头
		Collection<String> columnNames = new ArrayList<String>();
		for (int i = 0; i < 5; i++)
			columnNames.add("标题" + 1);
		map.put("columnNames", columnNames);

		// 表格数据
		Collection<Collection<String>> rows = new ArrayList<Collection<String>>();
		Collection<String> row;
		for (int j = 0; j < 2; j++) {
			row = new ArrayList<String>();
			for (int i = 0; i < 5; i++) {
				row.add(j + ":" + i);
			}
			rows.add(row);
		}
		map.put("rowDatas", rows);
		return map;
	}
}
