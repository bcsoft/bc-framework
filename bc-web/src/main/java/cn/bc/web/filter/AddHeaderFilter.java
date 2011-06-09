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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 给请求文件添加额外Header的过滤器
 *  
 * @author CD826
 * @since 1.0.0
 */
public class AddHeaderFilter implements Filter {
	Map<String,String> headers = new HashMap<String,String>();
	
	public void init(FilterConfig config) throws ServletException {
		String headersStr = config.getInitParameter("headers");
		String[] headers = headersStr.split(",");
		for(int i = 0; i < headers.length; i++) {
			String[] temp = headers[i].split("=");
			this.headers.put(temp[0].trim(), temp[1].trim());
		}
	}
	
	public void destroy() { }

	public void doFilter(ServletRequest req, ServletResponse res,
						 FilterChain chain) throws IOException, ServletException {
		if(req instanceof HttpServletRequest) {
			doFilter((HttpServletRequest)req, (HttpServletResponse)res, chain);
		}else {
			chain.doFilter(req, res);
		}
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, 
						  FilterChain chain) throws IOException, ServletException {
		for(Iterator<Entry<String,String>> it = headers.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
			response.addHeader((String)entry.getKey(), (String)entry.getValue());
		}
		chain.doFilter(request, response);
	}
}