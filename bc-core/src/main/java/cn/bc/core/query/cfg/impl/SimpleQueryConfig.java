package cn.bc.core.query.cfg.impl;

import cn.bc.core.query.condition.Condition;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简易查询的封装
 * Created by dragon on 2014/6/16.
 */
public class SimpleQueryConfig extends AbstractQueryConfig {
    public SimpleQueryConfig() {
        super();
    }

    /**
     * @param sqlTpl 查询语句模板
     * @param params 查询参数
     */
    public SimpleQueryConfig(String sqlTpl, List<Object> params) {
        super(sqlTpl, params);
    }

    public String getQueryString(Condition condition) {
        Assert.hasText(this.queryStringTpl, "queryStringTpl is empty.");

        Map<String, Object> args = new HashMap<String, Object>();
        if (condition != null) {
            String e = condition.getExpression();
            if(e != null && !e.isEmpty()) args.put("condition", condition.getExpression());
        }
        return format(this.queryStringTpl, args);
    }
}