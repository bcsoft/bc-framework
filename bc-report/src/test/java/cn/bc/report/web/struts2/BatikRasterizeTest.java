package cn.bc.report.web.struts2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.batik.apps.rasterizer.Main;
import org.junit.Test;

public class BatikRasterizeTest {
	@Test
	public void toJpg() {
		convert("image/jpeg", "jpg");
	}

	@Test
	public void toPng() {
		convert("image/png", "png");
	}

	@Test
	public void toPdf() {
		convert("application/pdf", "pdf");
	}

	@Test
	public void toTiff() {
		convert("image/tiff", "tif");
	}

	private void convert(String type, String ext) {
		String from = "d:/test.svg";
		String to = "d:/test." + ext;
		List<String> args = new ArrayList<String>();

		// 转换为的格式,值为image/png、image/jpeg、application/pdf、image/svg xml
		args.add("-m");
		args.add(type);

		args.add("-d");// 输出文件
		args.add(to);

		args.add("-w");// 图片宽度
		args.add("400");

		// 必须加上这个，否者报错：java.security.AccessControlException
		args.add("-scriptSecurityOff");

		args.add(from);// 要转换的svg文件

		Assert.assertTrue(new File(from).exists());

		// 如果文件存在先删除
		File file = new File(to);
		if (file.exists())
			file.delete();
		Assert.assertFalse(file.exists());

		// 执行转换
		new Main(args.toArray(new String[0])).execute();

		// Runtime.getRuntime().exec(command);
		Assert.assertTrue(new File(to).exists());
	}
}
