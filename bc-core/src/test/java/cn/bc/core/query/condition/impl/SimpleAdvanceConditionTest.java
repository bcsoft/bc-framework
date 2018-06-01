/**
 *
 */
package cn.bc.core.query.condition.impl;

import cn.bc.core.query.QueryOperator;
import cn.bc.core.util.DateUtils;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

;

/**
 * @author dragon
 */
public class SimpleAdvanceConditionTest {
  @Test
  public void range_null_date() {
    JsonArray array = Json.createArrayBuilder()
      .add("moveDate")
      .add(Json.createArrayBuilder()
        .add((String) null)
        .add("2016-11-01"))
      .add("date")
      .add("[]").build();
    //System.out.println(array.toString());
    SimpleAdvanceCondition c = new SimpleAdvanceCondition(array);
    assertEquals("moveDate", c.getId());
    assertEquals(QueryOperator.Range, c.getOperator());
    assertTrue(c.getValue() instanceof List);
    assertNull(((List) c.getValue()).get(0));
    assertTrue(((List) c.getValue()).get(1) instanceof Date);
    assertEquals("2016-11-01", DateUtils.formatDate((Date) ((List) c.getValue()).get(1)));
  }

  @Test
  public void range_date() {
    JsonArray array = Json.createArrayBuilder()
      .add("moveDate")
      .add(Json.createArrayBuilder()
        .add("2016-11-01"))
      .add("date")
      .add("[]").build();
    //System.out.println(array.toString());
    SimpleAdvanceCondition c = new SimpleAdvanceCondition(array);
    assertEquals("moveDate", c.getId());
    assertEquals(QueryOperator.Range, c.getOperator());
    assertTrue(c.getValue() instanceof List);
    assertTrue(((List) c.getValue()).get(0) instanceof Date);
    assertTrue(((List) c.getValue()).size() == 1);
    assertEquals("2016-11-01", DateUtils.formatDate((Date) ((List) c.getValue()).get(0)));
  }

  @Test
  public void singleStringValue() {
    JsonArray array = Json.createArrayBuilder().add("company").add("3").build();
    //System.out.println(array.toString());
    SimpleAdvanceCondition c = new SimpleAdvanceCondition(array);
    assertEquals("company", c.getId());
    assertEquals(QueryOperator.Equals, c.getOperator());
    assertTrue(c.getValue() instanceof String);
    assertEquals("3", c.getValue());
  }
}
