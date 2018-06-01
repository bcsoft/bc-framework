package cn.bc.template.word.doc;

import org.apache.poi.hwpf.HWPFDocument;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DocumentCopyTest {
  // 加载xml文档的内容
  private HWPFDocument loadDocument() throws Exception {
    InputStream is = new ClassPathResource(
      "cn/bc/template/word/docTpl.doc").getInputStream();
    HWPFDocument document = new HWPFDocument(is);
    return document;
  }

  @Test
  public void testFileCopy() throws Exception {
    HWPFDocument document = this.loadDocument();

    // 输出word文件
    OutputStream os = new FileOutputStream("/t/docTpl_copy.doc");
    document.write(os);
    os.close();
  }
}
