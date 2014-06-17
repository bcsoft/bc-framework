package cn.bc.core.query.cfg.impl;

import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dragon on 2014/6/16.
 */
public class SimpleQueryConfigTest {
    @Test
    public void test01() {
        String tpl = "select * from t<#if condition??> where ${condition}</#if>";
        SimpleQueryConfig cfg = new SimpleQueryConfig(tpl, null);
        Assert.assertEquals("select * from t", cfg.getQueryString(null));
        Assert.assertNotNull(cfg.getQueryParams(null));
        Assert.assertEquals(0, cfg.getQueryParams(null).size());

        List<Object> params = new ArrayList<Object>();
        params.add(100);
        cfg = new SimpleQueryConfig(tpl, params);
        Condition c = new EqualsCondition("id", 100);
        Assert.assertEquals("select * from t where id = ?", cfg.getQueryString(c));
        Assert.assertNotNull(cfg.getQueryParams(null));
        Assert.assertEquals(1, cfg.getQueryParams(null).size());
        Assert.assertEquals(100, cfg.getQueryParams(null).get(0));
    }
}