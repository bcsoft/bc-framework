package cn.bc.core.query.cfg.impl;

import cn.bc.core.query.cfg.QueryConfig;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.MixCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dragon on 2014/6/16.
 */
public class PagingQueryConfig extends SimpleQueryConfig implements cn.bc.core.query.cfg.PagingQueryConfig {
    protected String totalnumQueryStringTpl;        // 总数查询语句模板
    protected List<Object> totalnumQueryParams = new ArrayList<Object>();       // 总数查询语句参数
	protected Map<String, Object> templateParams = new HashMap<String, Object>();   // SQL模板参数
    private int offset = 0;
    private int limit = 0;

    public PagingQueryConfig() {
        super();
    }

    public PagingQueryConfig(String queryStringTpl, List<Object> queryPparams) {
        super(queryStringTpl, queryPparams);
    }

    public PagingQueryConfig(String queryStringTpl, List<Object> queryPparams, boolean autoTotalQuery) {
        super(queryStringTpl, queryPparams);
        if (autoTotalQuery) {
            this.totalnumQueryStringTpl = buildTotalnumQueryStringTpl(this.queryStringTpl);
            this.addTotalnumQueryParams(queryPparams);
        }
    }

    protected String buildTotalnumQueryStringTpl(String queryStringTpl) {
        throw new UnsupportedOperationException("method not implements.");
    }

    public PagingQueryConfig(String sqlTpl, String totalnumQueryStringTpl, List<Object> queryPparams) {
        super(sqlTpl, queryPparams);
        this.totalnumQueryStringTpl = totalnumQueryStringTpl;
        this.addTotalnumQueryParams(queryPparams);  // 默认与queryPparams相同
    }

    public int getOffset() {
        return this.offset;
    }

    public PagingQueryConfig setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getLimit() {
        return this.limit;
    }

    public PagingQueryConfig setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public String getQueryString(Condition condition) {
        Assert.hasText(this.queryStringTpl, "queryStringTpl is empty.");

        Map<String, Object> args = new HashMap<String, Object>();
        if (condition != null){
            String e = condition.getExpression();
            if(e != null && !e.isEmpty()) {
                if(!e.toLowerCase().startsWith("order by")) {
                   e = "where " + e;
                }
                args.put("condition", e);
            }
        }
        if (offset > 0) args.put("offset", this.getOffset());
        if (limit > 0) args.put("limit", Math.max(1, this.getLimit()));
		if(!this.templateParams.isEmpty()) args.putAll(this.templateParams);
        return format(this.queryStringTpl, args);
    }

    public String getTotalnumQueryString(Condition condition) {
        Assert.hasText(this.totalnumQueryStringTpl, "totalnumQueryStringTpl is empty.");

        Map<String, Object> args = new HashMap<String, Object>();
        if (condition != null){
            String e;
            if (condition instanceof MixCondition){
                // 排除 Orderby 条件
                e = ((MixCondition)condition).getExpression(null, true);
            }else if (condition instanceof OrderCondition){
                e = null;
            }else{
                e = condition.getExpression();
            }
            if(e != null && !e.isEmpty()) {
                if(!e.toLowerCase().startsWith("order by")) {
                    e = "where " + e;
                }
                args.put("condition", e);
            }
        }
		if(!this.templateParams.isEmpty()) args.putAll(this.templateParams);
        return format(this.totalnumQueryStringTpl, args);
    }

    public List<Object> getTotalnumQueryParams(Condition condition) {
        List<Object> params = new ArrayList<Object>();
        if (this.totalnumQueryParams != null) params.addAll(this.totalnumQueryParams);
        if (condition != null) {
            List<Object> conditionValues = condition.getValues();
            if (conditionValues != null) params.addAll(conditionValues);
        }
        return params;
    }

    /**
     * 设置计数查询语句模板
     *
     * @param sqlTpl 原始计数查询语句模板
     * @return this
     */
    public PagingQueryConfig setTotalnumQueryStringTpl(String sqlTpl) {
        this.totalnumQueryStringTpl = sqlTpl;
        return this;
    }

    /**
     * 添加计数查询参数
     *
     * @param param 查询参数
     * @return this
     */
    public PagingQueryConfig addTotalnumQueryParam(Object param) {
        this.totalnumQueryParams.add(param);
        return this;
    }

    /**
     * 批量添加计数查询参数
     *
     * @param params 查询参数
     * @return this
     */
    public PagingQueryConfig addTotalnumQueryParams(List<Object> params) {
        if (params != null)
            this.totalnumQueryParams.addAll(params);
        return this;
    }

	/**
	 * 添加SQL模板参数
	 *
	 * @param key 键
	 * @param value 值
	 * @return this
	 */
	public QueryConfig addTemplateParam(String key, Object value) {
		this.templateParams.put(key, value);
		return this;
	}

	/**
	 * 添加SQL模板参数
	 *
	 * @param params 参数
	 * @return this
	 */
	public QueryConfig addTemplateParam(Map<String, Object> params) {
		this.templateParams.putAll(params);
		return this;
	}
}
