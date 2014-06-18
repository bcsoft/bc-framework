package cn.bc.core.query.cfg.impl;

import cn.bc.core.query.cfg.QueryConfig;
import cn.bc.core.query.condition.Condition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL查询语句及参数配置的简易抽象实现
 * Created by dragon on 2014/6/16.
 */
public abstract class AbstractQueryConfig implements QueryConfig {
    protected static final Log logger = LogFactory.getLog("cn.bc.core.query.cfg.QueryConfig");
    protected String queryStringTpl;            // 查询语句模板
    protected List<Object> queryParams = new ArrayList<Object>();   // 查询参数

    public AbstractQueryConfig() {
    }

    /**
     * @param queryStringTpl 查询语句模板
     * @param params 查询参数
     */
    public AbstractQueryConfig(String queryStringTpl, List<Object> params) {
        this.queryStringTpl = queryStringTpl;
        this.addQueryParams(params);
    }

    public List<Object> getQueryParams(Condition condition) {
        List<Object> params = new ArrayList<Object>();
        if (this.queryParams != null) params.addAll(this.queryParams);
        if (condition != null) {
            List<Object> conditionValues = condition.getValues();
            if (conditionValues != null) params.addAll(conditionValues);
        }
        return params;
    }

    /**
     * 添加查询参数
     *
     * @param param 参数
     * @return this
     */
    public QueryConfig addQueryParam(Object param) {
        this.queryParams.add(param);
        return this;
    }

    /**
     * 批量添加查询参数
     *
     * @param params 参数
     * @return this
     */
    public QueryConfig addQueryParams(List<Object> params) {
        if (params != null)
            this.queryParams.addAll(params);
        return this;
    }

    /**
     * 设置数据查询语句模板
     *
     * @param queryStringTpl 查询语句模板
     * @return this
     */
    public QueryConfig setQueryStringTpl(String queryStringTpl) {
        this.queryStringTpl = queryStringTpl;
        return this;
    }

    // 使用 freemarker 格式化模板的处理封装
    public static String format(String tpl, Object args) {
        if (tpl == null || tpl.length() == 0)
            return "";

        // 构建模板
        try {
            freemarker.template.Template template = new freemarker.template
                    .Template("SimpleQueryConfig", new StringReader(tpl), null);

            // 合并数据模型与模板
            Writer out = new StringWriter();
            template.process(args, out);
            out.flush();
            out.close();

            return out.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}