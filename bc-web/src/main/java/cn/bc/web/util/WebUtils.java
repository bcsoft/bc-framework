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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.bc.core.exception.CoreException;

/**
 * WebUI的辅助函数库
 * 
 * @author dragon
 * @since 1.0.0
 */
public class WebUtils implements ServletContextAware {
	static Log logger = LogFactory.getLog(WebUtils.class);
	private static ServletContext servletContext = null;
	private static WebApplicationContext wac = null;

	private WebUtils() {
	}

	public void setServletContext(ServletContext _servletContext) {
		servletContext = _servletContext;

		// 获取web应用访问的上下文路径:部署到根目录为"",否则为"/[appName]"
		rootPath = servletContext.getRealPath("/");
		if (null == rootPath)
			throw new CoreException("Error occured when getting context path.");
		logger.fatal("rootPath=" + rootPath);
	}

	public static String rootPath = "";// File.separator

	/**
	 * 设置系统访问的上下文路径
	 * <p>
	 * 这个需要在系统初始化时设置
	 * </p>
	 * 
	 * @param rootPath
	 */
	public static void setRootPath(String rootPath) {
		WebUtils.rootPath = rootPath;
	}

	/**
	 * 生成CSS的输出语句
	 * 
	 * @param cssPath
	 *            所要输出的CSS路径
	 * @return 该CSS输出的HTML语句
	 */
	public static String printCSS(String cssPath) {
		return "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssPath
				+ "\"/>";
	}

	/**
	 * 生成JS的输出语句
	 * 
	 * @param jsPath
	 *            所要输出的CSS路径
	 * @return 该JS输出的HTML语句
	 */
	public static String printJS(String jsPath) {
		return "<script type=\"text/javascript\" src=\"" + jsPath
				+ "\"></script>";
	}

	/**
	 * 获取请求的资源的路径信息
	 * 
	 * <pre>
	 * http://www.demo.com/demo/test.htm  -->  /demo/test.htm</span>
	 * </pre>
	 * 
	 * @param request
	 * @return 返回格式如/demo/test.htm
	 */
	public static String getResourcePath(HttpServletRequest request) {
		// Adapted from VelocityViewServlet.handleRequest() method:

		// If we get here from RequestDispatcher.include(), getServletPath()
		// will return the original (wrong) URI requested. The following
		// special attribute holds the correct path. See section 8.3 of the
		// Servlet 2.3 specification.

		String path = (String) request
				.getAttribute("javax.servlet.include.servlet_path");

		// Also take into account the PathInfo stated on
		// SRV.4.4 Request Path Elements.
		String info = (String) request
				.getAttribute("javax.servlet.include.path_info");

		if (path == null) {
			path = request.getServletPath();
			info = request.getPathInfo();
		}

		if (info != null) {
			path += info;
		}

		return path;
	}

	/**
	 * 包装指定的js函数：函数上下文及参数不变，但函数可以延时定义
	 * 
	 * @param fnName
	 *            要包装的js函数名
	 * @return 包装好的函数
	 */
	public static String wrapJSFunction(String fnName) {
		return "function(){return window['" + fnName
				+ "'].apply(this,arguments);}";
	}

	public static String wrapJSFunctionWithVar(String fnName, String varName) {
		if (!StringUtils.hasLength(varName))
			return "function(){return window['" + fnName
					+ "'].apply(this,arguments);}";
		else
			return "function(){this.pvar='" + varName + "';return window['"
					+ fnName + "'].apply(this,arguments);}";
	}

	public static String encodeFileName(HttpServletRequest request,
			String srcFileName) {
		boolean isIE = request.getHeader("User-Agent").toUpperCase()
				.indexOf("MSIE") != -1;
		return encodeFileName(isIE, srcFileName);
	}

	/**
	 * 重新编码下载文件的文件名，保证中文不乱码
	 * 
	 * @param isIE
	 * @param srcFileName
	 *            原文件名
	 * @return 编码后的文件名
	 */
	public static String encodeFileName(boolean isIE, String srcFileName) {
		String _fileName;
		// 解决中文文件名乱码问题
		// boolean isIE = request.getHeader("User-Agent").toUpperCase()
		// .indexOf("MSIE") != -1;
		try {
			if (isIE) {// IE核心的浏览器
				_fileName = URLEncoder.encode(srcFileName, "UTF-8");
				if (_fileName.length() > 150) {
					// 用URLEncoder编码，当中文文字超过17个时，IE6
					// 无法下载文件。这是IE的bug，参见微软的知识库文章KB816868
					// 微软提供了一个补丁，这个补丁需要先安装ie6 sp1
					String guessCharset = "gb2312"; // 根据request的locale得出可能的编码，中文操作系统通常是gb2312
					_fileName = new String(srcFileName.getBytes(guessCharset),
							"ISO8859-1");
				}
			} else {// 非IE核心的浏览器:不能使用URLEncoder.encode，否则文件名为一长串的url编码
				_fileName = new String(srcFileName.getBytes("UTF-8"),
						"ISO8859-1");
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn(e.getMessage());
			_fileName = srcFileName;
		}
		return _fileName;
	}

	/**
	 * 获取产生请求的客户端IP地址信息
	 * 
	 * @param request
	 * @return
	 */
	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");// apache转发
		logger.debug("get clientIP by request.getHeader(\"x-forwarded-for\")");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");// 使用代理
			logger.debug("get clientIP by request.getHeader(\"Proxy-Client-IP\")");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");// weblogic集群
			logger.debug("get clientIP by request.getHeader(\"WL-Proxy-Client-IP\")");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();// 原始
			logger.debug("get clientIP by request.getRemoteAddr()");
		}
		return ip;
	}

	/**
	 * 获取指定ip的mac地址
	 * 
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static String getMac(String ip) throws Exception {
		if ("127.0.0.1".equals(ip) || "localhost".equals(ip))
			return "untrace because localhost";
		return new UdpGetClientMacAddr(ip).GetRemoteMacAddr();
	}

	/**
	 * 获取 WebApplicationContext 对象
	 * 
	 * @return
	 */
	public static synchronized WebApplicationContext getWac() {
		if (null == wac)
			wac = WebApplicationContextUtils
					.getRequiredWebApplicationContext(servletContext);
		return wac;
	}

	/**
	 * 获取Spring对象
	 * 
	 * @param name
	 *            bean的配置名称
	 * @return bean对象
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return getWac().getBean(name, requiredType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return getWac().getBean(requiredType);
	}

	/**
	 * 获取客户端浏览器的标识信息
	 * 
	 * @param request
	 * @return
	 */
	public static String getBrowser(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}
}