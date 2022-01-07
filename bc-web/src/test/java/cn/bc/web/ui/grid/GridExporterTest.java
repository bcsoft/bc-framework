package cn.bc.web.ui.grid;

import cn.bc.core.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public class GridExporterTest {
  /**
   * 测试使用jxls基于excel模板文件导出excel文件
   *
   * @throws Exception
   */
  @Test
  public void test01() throws Exception {
    // 使用默认的模板
    InputStream is = new ClassPathResource("cn/bc/web/template/export.xlsx")
      .getInputStream();

    Map<String, Object> map = buildData();
    File fileOut = new File("target/export.xlsx");
    if (fileOut.exists()) fileOut.delete();
    JxlsHelper.getInstance().setEvaluateFormulas(true).processTemplate(
      is,
      new FileOutputStream(fileOut),
      new Context(map)
    );
    Assert.assertTrue(fileOut.exists());
    Assert.assertTrue(fileOut.length() > 0);
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
