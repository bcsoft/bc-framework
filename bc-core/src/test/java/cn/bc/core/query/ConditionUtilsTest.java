package cn.bc.core.query;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.util.DateUtils;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ConditionUtilsTest {
//	private static Logger logger = LoggerFactory.getLogger(ConditionUtilsTest.class);

	@Test
	public void fuzzySearch_emptyQuery() throws Exception {
		assertNull(ConditionUtils.toFuzzyCondition(null, null, false));
		assertNull(ConditionUtils.toFuzzyCondition(null, null, true));
		assertNull(ConditionUtils.toFuzzyCondition(null, new String[]{}, false));
		assertNull(ConditionUtils.toFuzzyCondition(null, new String[]{}, true));
		assertNull(ConditionUtils.toFuzzyCondition(null, new String[]{"k"}, false));
		assertNull(ConditionUtils.toFuzzyCondition(null, new String[]{"k"}, true));

		assertNull(ConditionUtils.toFuzzyCondition("", null, false));
		assertNull(ConditionUtils.toFuzzyCondition("", null, true));
		assertNull(ConditionUtils.toFuzzyCondition("", new String[]{}, false));
		assertNull(ConditionUtils.toFuzzyCondition("", new String[]{}, true));
		assertNull(ConditionUtils.toFuzzyCondition("", new String[]{"k"}, false));
		assertNull(ConditionUtils.toFuzzyCondition("", new String[]{"k"}, true));

		assertNull(ConditionUtils.toFuzzyCondition("a", null, false));
		assertNull(ConditionUtils.toFuzzyCondition("a", null, true));
	}

	@Test
	public void fuzzySearch_oneLike() throws Exception {
		Condition c = ConditionUtils.toFuzzyCondition("v", new String[]{"k"}, false);
		assertNotNull(c);
		assertEquals("k like ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v%", args.get(0));

		c = ConditionUtils.toFuzzyCondition("%v", new String[]{"k"}, false);
		assertNotNull(c);
		assertEquals("k like ?", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v", args.get(0));

		c = ConditionUtils.toFuzzyCondition("v%", new String[]{"k"}, false);
		assertNotNull(c);
		assertEquals("k like ?", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("v%", args.get(0));
	}

	@Test
	public void fuzzySearch_twoLike() throws Exception {
		Condition c = ConditionUtils.toFuzzyCondition("v", new String[]{"k1", "k2"}, false);
		assertNotNull(c);
		assertEquals("(k1 like ? or k2 like ?)", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(2, args.size());
		assertEquals("%v%", args.get(0));
		assertEquals("%v%", args.get(1));

		c = ConditionUtils.toFuzzyCondition("%v", new String[]{"k1", "k2"}, false);
		assertNotNull(c);
		assertEquals("(k1 like ? or k2 like ?)", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(2, args.size());
		assertEquals("%v", args.get(0));
		assertEquals("%v", args.get(1));

		c = ConditionUtils.toFuzzyCondition("v%", new String[]{"k1", "k2"}, false);
		assertNotNull(c);
		assertEquals("(k1 like ? or k2 like ?)", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(2, args.size());
		assertEquals("v%", args.get(0));
		assertEquals("v%", args.get(1));
	}

	@Test
	public void advanceSearch_emptyQuery() throws Exception {
		assertNull(ConditionUtils.toCondition(null, null, false));
		assertNull(ConditionUtils.toCondition(null, null, true));
		assertNull(ConditionUtils.toCondition(null, new String[]{}, false));
		assertNull(ConditionUtils.toCondition(null, new String[]{}, true));
		assertNull(ConditionUtils.toCondition(null, new String[]{"k"}, false));
		assertNull(ConditionUtils.toCondition(null, new String[]{"k"}, true));

		assertNull(ConditionUtils.toCondition("", null, false));
		assertNull(ConditionUtils.toCondition("", null, true));
		assertNull(ConditionUtils.toCondition("", new String[]{}, false));
		assertNull(ConditionUtils.toCondition("", new String[]{}, true));
		assertNull(ConditionUtils.toCondition("", new String[]{"k"}, false));
		assertNull(ConditionUtils.toCondition("", new String[]{"k"}, true));

		assertNull(ConditionUtils.toCondition("a", null, false));
		assertNull(ConditionUtils.toCondition("a", null, true));
		assertNull(ConditionUtils.toCondition("[]", null, true));
	}

	@Test
	public void advanceSearch_equals() throws Exception {
		JsonArray qs = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k1")
						.add("operator", "=")
						.add("value", "v")
						.add("type", "string"))
				.build();
		Condition c = ConditionUtils.toCondition(qs.toString(), null, false);
		assertNotNull(c);
		assertEquals("k1 = ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("v", args.get(0));

		qs = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k1")
						.add("operator", "=")
						.add("value", "v"))
				.add(Json.createObjectBuilder()
						.add("id", "k2")
						.add("operator", "=")
						.add("value", "1")
						.add("type", "int"))
				.build();
		c = ConditionUtils.toCondition(qs.toString(), null, false);
		assertNotNull(c);
		assertEquals("(k1 = ? and k2 = ?)", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(2, args.size());
		assertEquals("v", args.get(0));
		assertEquals(1, args.get(1));
	}

	@Test
	public void advanceSearch_gt() throws Exception {
		JsonArray qs = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k1")
						.add("operator", ">=")
						.add("value", "2016-01-01")
						.add("type", "date"))
				.build();
		Condition c = ConditionUtils.toCondition(qs.toString(), null, false);
		assertNotNull(c);
		assertEquals("k1 >= ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals(Date.class, args.get(0).getClass());
		assertEquals("2016-01-01", DateUtils.formatDateTime((Date) args.get(0), "yyyy-MM-dd"));
	}

	@Test
	public void mixSearch() throws Exception {
		JsonArray qs = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k1")
						.add("operator", "=")
						.add("value", "v"))
				.add(Json.createObjectBuilder()
						.add("id", "_fuzzy_")
						.add("value", "a"))
				.build();
		Condition c = ConditionUtils.toCondition(qs.toString(), null, false);
		assertNotNull(c);
		assertEquals("k1 = ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("v", args.get(0));

		c = ConditionUtils.toCondition(qs.toString(), new String[]{"f1"}, false);
		assertNotNull(c);
		assertEquals("(k1 = ? and f1 like ?)", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(2, args.size());
		assertEquals("v", args.get(0));
		assertEquals("%a%", args.get(1));

		c = ConditionUtils.toCondition(qs.toString(), new String[]{"f1", "f2"}, false);
		assertNotNull(c);
		assertEquals("(k1 = ? and (f1 like ? or f2 like ?))", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(3, args.size());
		assertEquals("v", args.get(0));
		assertEquals("%a%", args.get(1));
		assertEquals("%a%", args.get(2));
	}
}