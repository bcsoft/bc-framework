package cn.bc.test;

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.DigestUtils;

public class MD5Test {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws UnsupportedEncodingException {
		String passwordMD5 = "5f4dcc3b5aa765d61d8327deb882cf99";
		Assert.assertEquals(passwordMD5, DigestUtils
				.md5DigestAsHex("password".getBytes()));
		Assert.assertEquals(passwordMD5, DigestUtils.md5DigestAsHex("password"
				.getBytes("utf-8")));
		Assert.assertEquals(passwordMD5, DigestUtils.md5DigestAsHex("password"
				.getBytes("gbk")));
	}
}
