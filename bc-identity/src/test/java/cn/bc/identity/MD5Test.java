package cn.bc.identity;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.util.DigestUtils;

public class MD5Test {
	@Test
	public void test01() throws Exception {
		Assert.assertEquals(32, "21218cca77804d2ba1922c33e0151105".length());
		Assert.assertEquals("21218cca77804d2ba1922c33e0151105",
				DigestUtils.md5DigestAsHex("888888".getBytes("UTF-8")));
		Assert.assertEquals("f379eaf3c831b04de153469d1bec345e",
				DigestUtils.md5DigestAsHex("666666".getBytes("UTF-8")));
		Assert.assertEquals("5f4dcc3b5aa765d61d8327deb882cf99",
				DigestUtils.md5DigestAsHex("password".getBytes("UTF-8")));
	}
}
