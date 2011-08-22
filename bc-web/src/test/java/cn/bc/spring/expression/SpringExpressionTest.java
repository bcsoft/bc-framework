package cn.bc.spring.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpringExpressionTest {
	ExpressionParser parser = new SpelExpressionParser();
	Expression exp;
	EvaluationContext context;;

	@Test
	public void simple() {
		exp = parser.parseExpression("'Hello World'");
		Assert.assertEquals("Hello World", exp.getValue());

		exp = parser.parseExpression("'Hello World'.concat('!')");
		Assert.assertEquals("Hello World!", exp.getValue());

		exp = parser.parseExpression("'Hello World'.bytes.length");
		Assert.assertEquals("Hello World".length(), exp.getValue());

		exp = parser.parseExpression("new String('hello world').toUpperCase()");
		Assert.assertEquals("Hello World".toUpperCase(), exp.getValue());

		Domain obj = new Domain(false, 1, "n1");
		context = new StandardEvaluationContext(obj);
		exp = parser.parseExpression("'Hello World'");
		Assert.assertEquals("Hello World", exp.getValue(context));
	}

	@Test
	public void varContext() {
		Domain obj = new Domain(false, 1, "n1");
		context = new StandardEvaluationContext();
		context.setVariable("domain", obj);

		exp = parser.parseExpression("#domain.code");
		Object value = exp.getValue(context);
		Assert.assertEquals(obj.getCode(), value);

		exp = parser.parseExpression("#domain.name");
		value = exp.getValue(context);
		Assert.assertEquals("n1", value);
	}

	@Test
	public void domainContext() {
		Domain obj = new Domain(false, 1, "n1");
		context = new StandardEvaluationContext(obj);

		exp = parser.parseExpression("code");
		Object value = exp.getValue(context);
		Assert.assertEquals(obj.getCode(), value);

		exp = parser.parseExpression("code==1");
		value = exp.getValue(context);
		Assert.assertSame(true, value);

		exp = parser.parseExpression("name=='n1'");
		value = exp.getValue(context);
		Assert.assertSame(true, value);

		exp = parser.parseExpression("code==1 and name=='n1'");
		value = exp.getValue(context);
		Assert.assertSame(true, value);

		exp = parser.parseExpression("code + name");//合并属性
		value = exp.getValue(context);
		Assert.assertEquals("1n1", value);
	}

	@Test
	public void domainListContext() {
		List<Domain> list = new ArrayList<Domain>();
		list.add(new Domain(false, 1, "n1"));
		list.add(new Domain(true, 2, "n2"));
		context = new StandardEvaluationContext(list);

		exp = parser.parseExpression("[0]");
		Domain value = exp.getValue(context, Domain.class);
		Assert.assertEquals(1, value.getCode());

		exp = parser.parseExpression("[0].code");
		int c = exp.getValue(context, Integer.class);
		Assert.assertEquals(1, c);

		exp = parser.parseExpression("[0].name");
		String n = exp.getValue(context, String.class);
		Assert.assertEquals("n1", n);
	}

	@Test
	public void arrayContext() {
		Object[] array = new Object[2];
		array[0] = 1;
		array[1] = "n1";
		context = new StandardEvaluationContext(array);

		exp = parser.parseExpression("[0]");
		Assert.assertEquals(1, exp.getValue(context));

		exp = parser.parseExpression("[1]");
		Assert.assertEquals("n1", exp.getValue(context));

		exp = parser.parseExpression("[0] + [1]");//合并数组元素
		Assert.assertEquals("1n1", exp.getValue(context));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void mapContext() {
		Map map = new HashMap();
		map.put("code", 1);
		map.put("name", "n1");
		context = new StandardEvaluationContext(map);

		exp = parser.parseExpression("['name']");
		Assert.assertEquals("n1", exp.getValue(context));

		exp = parser.parseExpression("[name]");
		Assert.assertEquals("n1", exp.getValue(context));

		exp = parser.parseExpression("['code']");
		Assert.assertEquals(1, exp.getValue(context));
	}

	class Domain {
		private boolean bool;
		private int code;
		private String name;

		public Domain(boolean bool, int code, String name) {
			this.bool = bool;
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}
	}
}
