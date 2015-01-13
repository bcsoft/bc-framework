package cn.bc.web.formater;

import org.junit.Assert;

import org.junit.Test;

public class LinkFormaterTest {

	@Test
	public void testSimple() {
		LinkFormater lf = new LinkFormater("/bs/carman/edit?id={0}", "user") {
			@Override
			public Object[] getParams(Object context, Object value) {
				Object[] args = new Object[1];
				args[0] = "111";
				return args;
			}
		};

		Object r = lf.format(null, "张三");
		Assert.assertEquals(
				"<a href=\"/bs/carman/edit?id=111\" class=\"bc-link\" data-mtype=\"user\">张三</a>",
				r);
	}

	@Test
	public void testWithExtras() {
		LinkFormater lf = new LinkFormater("/bs/carman/edit?id={0}", "user") {
			@Override
			public Object[] getParams(Object context, Object value) {
				Object[] args = new Object[1];
				args[0] = "111";
				return args;
			}

			@Override
			public String getTaskbarTitle(Object context, Object value) {
				return "用户 - 张三";
			}

			@Override
			public String getWinId(Object context, Object value) {
				return "user111";
			}
		};

		Object r = lf.format(null, "张三");
		Assert.assertEquals(
				"<a href=\"/bs/carman/edit?id=111\" class=\"bc-link\" data-mtype=\"user\" data-title=\"用户 - 张三\" data-mid=\"user111\">张三</a>",
				r);
	}
}
