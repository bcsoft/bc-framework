package cn.bc.core.query;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.core.util.DateUtils;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class ConditionUtilsTest {
	@Test
	public void toFuzzyConditionByEmptyQuery() throws Exception {
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
	public void toFuzzyConditionByOneLike() throws Exception {
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
	public void toFuzzyConditionByTwoLike() throws Exception {
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
	public void toConditionByEmptyQuery() throws Exception {
		assertNull(ConditionUtils.toCondition((String) null, null, false));
		assertNull(ConditionUtils.toCondition((String) null, null, true));
		assertNull(ConditionUtils.toCondition((String) null, new String[]{}, false));
		assertNull(ConditionUtils.toCondition((String) null, new String[]{}, true));
		assertNull(ConditionUtils.toCondition((String) null, new String[]{"k"}, false));
		assertNull(ConditionUtils.toCondition((String) null, new String[]{"k"}, true));

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
	public void toConditionByJsonArrayStringWithEquals() throws Exception {
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
	public void toConditionByJsonArrayStringWithGreaterThan() throws Exception {
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

	@Test(expected = IllegalArgumentException.class)
	public void toConditionByJsonArrayStringWithBadCustom() throws Exception {
		// missing operator
		JsonArray jsonArray = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k")
						.add("value", "v"))
				.build();
		ConditionUtils.toCondition(jsonArray.toString(), null);

		// missing value
		jsonArray = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "k")
						.add("operator", "="))
				.build();
		ConditionUtils.toCondition(jsonArray.toString(), null);
	}

	@Test
	public void toConditionByJsonArrayStringWithFuzzyAndEquals() throws Exception {
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

	@Test
	public void toConditionByJsonArrayStringWithFuzzyIdAndValue() throws Exception {
		JsonArray jsonArray = Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
						.add("id", "_fuzzy_")
						.add("value", "v"))
				.build();
		Condition c = ConditionUtils.toCondition(jsonArray.toString(), null, false);
		assertNull(c);

		c = ConditionUtils.toCondition(jsonArray.toString(), null, true);
		assertNull(c);

		c = ConditionUtils.toCondition(jsonArray.toString(), new String[]{}, false);
		assertNull(c);

		c = ConditionUtils.toCondition(jsonArray.toString(), new String[]{}, true);
		assertNull(c);

		c = ConditionUtils.toCondition(jsonArray.toString(), new String[]{"k"}, false);
		assertNotNull(c);
		assertEquals("k like ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v%", args.get(0));

		c = ConditionUtils.toCondition(jsonArray.toString(), new String[]{"k"}, true);
		assertNotNull(c);
		assertEquals("k ilike ?", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v%", args.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void toConditionByJsonArrayStringWithFuzzyIdButNoValue() throws Exception {
		JsonArray jsonArray = Json.createArrayBuilder()
				.add(Json.createObjectBuilder().add("id", "_fuzzy_"))
				.build();
		ConditionUtils.toCondition(jsonArray.toString(), null, false);
	}

	@Test
	public void toConditionByJsonStringWithFuzzyIdAndValue() throws Exception {
		JsonObject json = Json.createObjectBuilder()
				.add("id", "_fuzzy_")
				.add("value", "v")
				.build();
		Condition c = ConditionUtils.toCondition(json.toString(), null, false);
		assertNull(c);

		c = ConditionUtils.toCondition(json.toString(), null, true);
		assertNull(c);

		c = ConditionUtils.toCondition(json.toString(), new String[]{}, false);
		assertNull(c);

		c = ConditionUtils.toCondition(json.toString(), new String[]{}, true);
		assertNull(c);

		c = ConditionUtils.toCondition(json.toString(), new String[]{"k"}, false);
		assertNotNull(c);
		assertEquals("k like ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v%", args.get(0));

		c = ConditionUtils.toCondition(json.toString(), new String[]{"k"}, true);
		assertNotNull(c);
		assertEquals("k ilike ?", c.getExpression());
		args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("%v%", args.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void toConditionByJsonStringWithFuzzyIdButNoValue() throws Exception {
		JsonObject json = Json.createObjectBuilder().add("id", "_fuzzy_").build();
		ConditionUtils.toCondition(json.toString(), null, false);
	}

	@Test
	public void toConditionByJsonStringWithGoodCustom() throws Exception {
		JsonObject json = Json.createObjectBuilder()
				.add("id", "k")
				.add("operator", "=")
				.add("value", "v")
				.build();
		Condition c = ConditionUtils.toCondition(json.toString(), null);
		assertNotNull(c);
		assertEquals("k = ?", c.getExpression());
		List<Object> args = c.getValues();
		assertNotNull(args);
		assertEquals(1, args.size());
		assertEquals("v", args.get(0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void toConditionByJsonStringWithBadCustom() throws Exception {
		JsonObject json = Json.createObjectBuilder()
				.add("id", "k")
				.add("value", "v")
				.build();
		ConditionUtils.toCondition(json.toString(), null);
	}
}