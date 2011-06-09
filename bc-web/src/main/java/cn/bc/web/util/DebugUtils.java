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
package cn.bc.web.util;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WebUI的辅助函数库
 * 
 * @author dragon
 * @since 1.0.0
 */
public class DebugUtils {
	static Log logger = LogFactory.getLog(DebugUtils.class);

	public static StringBuffer getDebugInfo(HttpServletRequest request,
			HttpServletResponse response) {
		@SuppressWarnings("rawtypes")
		Enumeration e;
		String name;
		StringBuffer html = new StringBuffer();
		
		//session
		HttpSession session = request.getSession();
		html.append("<div><b>session:</b></div><ul>");
		html.append(createLI("Id",session.getId()));
		html.append(createLI("CreationTime",new Date(session.getCreationTime()).toString()));
		html.append(createLI("LastAccessedTime",new Date(session.getLastAccessedTime()).toString()));
		
		//session:attributes
		e = session.getAttributeNames();
		html.append("<li>attributes:<ul>\r\n");
		while(e.hasMoreElements()){
			name = (String)e.nextElement();
			html.append(createLI(name,String.valueOf(session.getAttribute(name))));
		}
		html.append("</ul></li>\r\n");
		html.append("</ul>\r\n");
		
		//request
		html.append("<div><b>request:</b></div><ul>");
		html.append(createLI("URL",request.getRequestURL().toString()));
		html.append(createLI("QueryString",request.getQueryString()));
		html.append(createLI("Method",request.getMethod()));
		html.append(createLI("CharacterEncoding",request.getCharacterEncoding()));
		html.append(createLI("ContentType",request.getContentType()));
		html.append(createLI("Protocol",request.getProtocol()));
		html.append(createLI("RemoteAddr",request.getRemoteAddr()));
		html.append(createLI("RemoteHost",request.getRemoteHost()));
		html.append(createLI("RemotePort",request.getRemotePort() + ""));
		html.append(createLI("RemoteUser",request.getRemoteUser()));
		html.append(createLI("ServerName",request.getServerName()));
		html.append(createLI("ServletPath",request.getServletPath()));
		html.append(createLI("ServerPort",request.getServerPort() + ""));
		html.append(createLI("Scheme",request.getScheme()));
		html.append(createLI("LocalAddr",request.getLocalAddr()));
		html.append(createLI("LocalName",request.getLocalName()));
		html.append(createLI("LocalPort",request.getLocalPort() + ""));
		html.append(createLI("Locale",request.getLocale().toString()));
		
		//request:headers
		e = request.getHeaderNames();
		html.append("<li>Headers:<ul>\r\n");
		while(e.hasMoreElements()){
			name = (String)e.nextElement();
			html.append(createLI(name,request.getHeader(name)));
		}
		html.append("</ul></li>\r\n");
		
		//request:parameters
		e = request.getParameterNames();
		html.append("<li>Parameters:<ul>\r\n");
		while(e.hasMoreElements()){
			name = (String)e.nextElement();
			html.append(createLI(name,request.getParameter(name)));
		}
		html.append("</ul></li>\r\n");
		
		html.append("</ul>\r\n");
		
		//response
		html.append("<div><b>response:</b></div><ul>");
		html.append(createLI("CharacterEncoding",response.getCharacterEncoding()));
		html.append(createLI("ContentType",response.getContentType()));
		html.append(createLI("BufferSize",response.getBufferSize()+""));
		html.append(createLI("Locale",response.getLocale().toString()));
		html.append("<ul>\r\n");
		return html;
	}

	private static String createLI(String key, String value) {
		return "<li>" + key + ": " + value + "</li>\r\n";
	}
}