/*
 * Copyright 2010- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bc.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 缓存控制过滤器。
 * 参数expires配置为-1代表永久缓存(10年)，配置为0代表禁用缓存，配置为大于0的值代表控制缓存多少分钟
 * <p/>
 * 要使用该过滤器，须在<tt>/WEB-INF/web.xml</tt>中配置，如下为配置永久缓存的范例:
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;cn.oz.web.filter.CacheFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.js&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.css&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.jpg&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.gif&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.png&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;servlet-name&gt;your-servlet&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;servlet&gt;
 *  &lt;servlet-name&gt;your-servlet&lt;/servlet-name&gt;
 * ... </pre>
 *
 * 若要排除某些目录或文件不作处理，
 * 需要在&lt;filter&gt;中增加如下参数配置(多个配置间用逗号分隔)：<br/>
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;foreverCacheFilter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;cn.oz.web.filter.CacheFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;
 *   &lt;param-name&gt;excludePaths&lt;/param-name&gt;
 *   &lt;param-value&gt;/demo1/*&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ... </pre>
 *  
 * @author dragon
 * @since 1.0.0
 */
public class CacheFilter extends CanExcludeFilter {
	/**
	 * 缓存时间配置
	 * 单位为分钟，适合http1.0以上的缓存参数
	 * 配为0代表禁用缓存,配为-1代表使用永久缓存
	 */
	private long expires;

	public void destroy() {
		super.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			// 仅对Http请求添加该过滤器的处理
			doFilter((HttpServletRequest) request,
					(HttpServletResponse) response, filterChain);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	public void doFilter(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		//排除处理
        if (isExcludePath((HttpServletRequest) request)) {
        	filterChain.doFilter(request, response);
            return;
        }

        //缓存控制
		if(expires != 0){
			long date = new java.util.Date().getTime();
			response.setDateHeader("Last-Modified", date);
			response.setDateHeader("Expires", date + expires);
			response.setHeader("Cache-Control", "public");
			response.setHeader("Pragma", "Pragma");
		}else{//不允许浏览器端缓存当前页面信息
			response.setHeader("Pragma","No-cache"); 
		    response.setHeader("Cache-Control","no-cache"); 
		    response.setDateHeader("Expires", 0); 
		}
		filterChain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		//初始化排除设置
		super.init(filterConfig);
		
		//初始化缓存配置：分钟转换为毫秒
		String _expires = filterConfig.getInitParameter("expires");
		if ("-1".equals(_expires)) {// 永久的缓存(设为10年)
			expires = 315360000000l;//10 * 365 * 24 * 60 * 60 * 1000
		} else {// 指定的缓存
			expires = Long.parseLong(_expires) * 60000l;
		}
	}
}
