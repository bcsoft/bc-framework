package cn.bc.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.web.util.WebUtils;


/**
 * 可排除某些目录或文件不作过滤处理的过滤器基类
 *
 * 在&lt;filter&gt;中增加如下参数配置(多个配置间用逗号分隔,目录要以*结尾，文件要以*开始)：<br/>
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;your-filter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;com.xxx.YourFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;
 *   &lt;param-name&gt;excludePaths&lt;/param-name&gt;
 *   &lt;param-value&gt;/demo1/*&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ... </pre>
 * 
 * @author dragon
 * @since 2010-07-16
 */
public abstract class CanExcludeFilter implements Filter {
	static Log logger = LogFactory.getLog(CanExcludeFilter.class);

    /** 要排除处理的文件夹配置 */
    protected List<String> excludeDirs = new ArrayList<String>();

    /** 要排除处理的文件配置 */
    protected List<String> excludeFiles = new ArrayList<String>();

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		//排除处理的文件、文件夹配置
		String param = filterConfig.getInitParameter("excludePaths");
		if (param != null) {
			String[] paths = StringUtils.tokenizeToStringArray(param, ",");
			String path;
			for (int i = 0; i < paths.length; i++) {
				path = paths[i].trim();
				if (path.endsWith("*")) {
					excludeDirs.add(path.substring(0, path.length() - 1));
				} else if (path.startsWith("*")) {
					excludeFiles.add(path.substring(1));
				} else {
					String message = "excludePaths'"
							+ path
							+ "'被忽略, 路径必须以符号*开始(文件)或结尾(文件夹)";
					logger.error(message);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		excludeDirs = null;
		excludeFiles = null;
	}

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
        if (isExcludePath((HttpServletRequest) servletRequest)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
	}
    
    /**
     * 判断请求的资源是否在排除处理的范围内
     * @param request 请求
     * @return 
     */
    protected boolean isExcludePath(HttpServletRequest request) {
    	return this.isExcludePath(WebUtils.getResourcePath(request));
    }
    
    /**
     * 判断请求的资源是否在排除处理的范围内
     * @param path 请求的资源的路径信息，格式如/demo/test.htm
     * @return 
     */
    protected boolean isExcludePath(String path) {
        if (!excludeFiles.isEmpty()) {
            for (int i = 0; i < excludeFiles.size(); i++) {
                String file = excludeFiles.get(i).toString();
                if (path.endsWith(file)) {
                    return true;
                }
            }
        }

        if (!excludeDirs.isEmpty()) {
            for (int i = 0; i < excludeDirs.size(); i++) {
                String dir = excludeDirs.get(i).toString();
                if (path.startsWith(dir)) {
                    return true;
                }
            }
        }

        return false;
    }
}
