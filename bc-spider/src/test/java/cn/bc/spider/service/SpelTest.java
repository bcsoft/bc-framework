package cn.bc.spider.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

public class SpelTest {
  private static Log logger = LogFactory.getLog(SpelTest.class);
  private ExpressionParser parser = new SpelExpressionParser();

  @Test
  public void testList() throws Exception {
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("k1", "v1");
    root.put("k2", "v2");
    String spel = "[k1]";

    Expression exp = parser.parseExpression(spel);
    StandardEvaluationContext context = new StandardEvaluationContext(root);
    Object v = exp.getValue(context);
    System.out.println("v.class=" + v.getClass());
    System.out.println("v=" + v);
  }

  @Test
  public void testMap() throws Exception {
    String spel = "(new java.util.HashMap<String, Object>()).put('k1','v1')";
    Expression exp = parser.parseExpression(spel);
    StandardEvaluationContext context = new StandardEvaluationContext();
    Object v = exp.getValue(context);
    System.out.println("v.class=" + v.getClass());
    System.out.println("v=" + v);
  }
}