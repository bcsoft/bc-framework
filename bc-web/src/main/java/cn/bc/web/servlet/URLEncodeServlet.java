package cn.bc.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.util.StringUtils;

/**
 * 通过URL传中文参数时的编码配置初始化Servlet
 * 
 * @author dragon
 */
public class URLEncodeServlet extends HttpServlet {
	private static final long serialVersionUID = 0L;
	private static Log logger = LogFactory.getLog(URLEncodeServlet.class);

	public void init(ServletConfig servletConfig) throws ServletException {
		boolean isEncodeToCN = "true".equalsIgnoreCase(servletConfig
				.getInitParameter("encodeToCN"));
		String fromEncode = servletConfig.getInitParameter("fromEncode");
		String toEncode = servletConfig.getInitParameter("toEncode");
		StringUtils.encodeToCN = isEncodeToCN;
		if (fromEncode != null && fromEncode.length() > 0)
			StringUtils.fromEncode = fromEncode;
		if (toEncode != null && toEncode.length() > 0)
			StringUtils.toEncode = toEncode;

		String msg = "StringUtils.encodeToCN = " + isEncodeToCN;
		msg += "; fromEncode = " + StringUtils.fromEncode + "; toEncode = "
				+ StringUtils.toEncode;
		logger.fatal(msg);

		super.init(servletConfig);
	}
}