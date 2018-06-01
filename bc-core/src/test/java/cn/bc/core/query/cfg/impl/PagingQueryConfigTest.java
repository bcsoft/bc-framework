package cn.bc.core.query.cfg.impl;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by dragon on 2014/6/16.
 */
public class PagingQueryConfigTest {
  @Test
  public void test01() {
    String tpl = "select * from t<#if condition??> where ${condition}</#if>";
    tpl += "<#if limit??> limit ${limit}</#if>";
    tpl += "<#if offset??> offset ${offset}</#if>";
    String totalnumTpl = "select count(*) from t<#if condition??> where ${condition}</#if>";
    PagingQueryConfig cfg = new PagingQueryConfig(tpl, totalnumTpl, null);
    Assert.assertEquals("select * from t limit 25", cfg.getQueryString(null));
    Assert.assertNotNull(cfg.getQueryParams(null));
    Assert.assertEquals(0, cfg.getQueryParams(null).size());
    Assert.assertEquals("select count(*) from t", cfg.getTotalnumQueryString(null));
    Assert.assertNotNull(cfg.getTotalnumQueryParams(null));
    Assert.assertEquals(0, cfg.getTotalnumQueryParams(null).size());
  }

  @Test
  public void test02() {
    String tpl = "select * from t<#if condition??> where ${condition}</#if>";
    tpl += "<#if limit??> limit ${limit}</#if>";
    tpl += "<#if offset??> offset ${offset}</#if>";
    String totalnumTpl = "select count(*) from t<#if condition??> where ${condition}</#if>";
    Condition c = new EqualsCondition("id", 100);
    PagingQueryConfig cfg = new PagingQueryConfig(tpl, totalnumTpl, null);

    Assert.assertEquals("select * from t where id = ? limit 25", cfg.getQueryString(c));
    Assert.assertNotNull(cfg.getQueryParams(c));
    Assert.assertEquals(1, cfg.getQueryParams(c).size());
    Assert.assertEquals("select count(*) from t where id = ?", cfg.getTotalnumQueryString(c));
    Assert.assertNotNull(cfg.getTotalnumQueryParams(c));
    Assert.assertEquals(1, cfg.getTotalnumQueryParams(c).size());
    Assert.assertEquals(100, cfg.getTotalnumQueryParams(c).get(0));
  }

  @Test
  public void test03() {
    String tpl = "select * from (select * from t<#if condition??> where ${condition}</#if>";
    tpl += "<#if limit??> limit ${limit}</#if>";
    tpl += "<#if offset??> offset ${offset}</#if>";
    tpl += ") t";
    String totalnumTpl = "select count(*) from t<#if condition??> where ${condition}</#if>";
    PagingQueryConfig cfg = new PagingQueryConfig(tpl, totalnumTpl, null);
    Assert.assertEquals("select * from (select * from t limit 25) t", cfg.getQueryString(null));
    Assert.assertNotNull(cfg.getQueryParams(null));
    Assert.assertEquals(0, cfg.getQueryParams(null).size());
    Assert.assertEquals("select count(*) from t", cfg.getTotalnumQueryString(null));
    Assert.assertNotNull(cfg.getTotalnumQueryParams(null));
    Assert.assertEquals(0, cfg.getTotalnumQueryParams(null).size());
  }
}