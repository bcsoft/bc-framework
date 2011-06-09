package cn.bc.web.filter.gzip;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.web.filter.CanExcludeFilter;
import cn.bc.web.util.WebUtils;


/**
 * 对HTML ServletResponse提供Gzip处理的过滤器。
 * 只有当响应的内容大于指定的阀值，响应的内容才会被压缩，默认的阀值大小为1024 bytes.
 * <p/>
 * 要使用该过滤器，须在<tt>/WEB-INF/web.xml</tt>中如下配置:
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;cn.oz.web.filter.gzip.GzipFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.js&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;*.css&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 *
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
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
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;cn.oz.web.filter.gzip.GzipFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;
 *   &lt;param-name&gt;excludePaths&lt;/param-name&gt;
 *   &lt;param-value&gt;/demo1/*&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ... </pre>
 *
 * 若要启用js、css文件的静态gzip支持(默认不支持)，
 * 需要在&lt;filter&gt;中增加如下参数配置：<br/>
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;gzip-filter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;cn.oz.web.filter.gzip.GzipFilter&lt;/filter-class&gt;
 *  &lt;init-param&gt;
 *   &lt;param-name&gt;enableStaticGzip&lt;/param-name&gt;
 *   &lt;param-value&gt;true&lt;/param-value&gt;
 *  &lt;/init-param&gt;
 * &lt;/filter&gt;
 * ... </pre>
 * 
 * @author dragon
 * @since 2010-07-16
 */
public class GzipFilter extends CanExcludeFilter {
	static Log logger = LogFactory.getLog(GzipFilter.class);
	/** 
	 * 执行gzip处理文件的最小阀值, 默认为1024 bytes。<p/>
	 * 可通过在web.xml中配置过滤器的参数minThreshold来设定特定的值。<p/>
	 * 通过在请求参数中添加gzip=false可以强制控制该请求的回应不使用压缩。
	 */
	protected int minThreshold = 1024;
	
	/**
	 * 是否允许对js、css文件使用静态gzip处理，默认为false。<p/>
	 * 若设为true，则js、css文件将自动转发到js.gz、css.gz文件,
	 * 因此系统必须预先将js、css压缩为js.gz、css.gz。
	 */
	protected boolean enableStaticGzip = false;
	
	/**
	 * 预先压缩好的js、css文件对应的压缩文件扩展名。
	 */
	protected String gzipExtend = "gz";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		//初始化排除配置
		super.init(filterConfig);
		
		//阀值参数配置
		String param = filterConfig.getInitParameter("minThreshold");
		if(StringUtils.hasLength(param)){
			this.minThreshold = Integer.parseInt(param);
		}
		
		//是启用js、css文件的静态压缩处理的配置
		param = filterConfig.getInitParameter("enableStaticGzip");
		if("true".equalsIgnoreCase(param)){
			this.enableStaticGzip = true;
		}
		
		//静态gzip处理文件的扩展名配置
		param = filterConfig.getInitParameter("gzipExtend");
		if(StringUtils.hasLength(param)){
			this.gzipExtend = param;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;
		String path = WebUtils.getResourcePath(request);//获取资源的路径，格式如/demo/test.htm
		
        if (isExcludePath(path)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        
		if (useGzip(request, response,path)) {
			if (enableStaticGzip
				&& (path.endsWith(".js") || path.endsWith(".css"))) {// 静态压缩
			    String realPath = path + this.gzipExtend;
			    if(logger.isDebugEnabled())
			    	logger.debug("redirec to:" + realPath);
			    //设置回应为压缩
			    setContentEncodingGZip(response);
			    //转发到对应的gz文件：*.js-->*.jsgz,*.css-->*.cssgz
                request.getRequestDispatcher(realPath).forward(servletRequest, servletResponse);
			} else {//动态压缩
				GzipServletResponseWrapper wrappedResponse = new GzipServletResponseWrapper(
						response, request);
				wrappedResponse.setCompressionThreshold(minThreshold);
				try {
					chain.doFilter(request, wrappedResponse);
				} finally {
					wrappedResponse.finishResponse();
				}
			}
		} else {
			chain.doFilter(request, response);
		}
	}
    
	/**判断请求的资源是否满足使用gzip的条件，判断的依据为：
	 * <p/>
	 * 1)header中还没有设置Content-Encoding的值；</p>
	 * 2)请求的参数中没有设置gzip=false；<p/>
	 * 3)请求的资源为非图片资源；<p/>
	 * 4)客户端的浏览器支持gzip功能；<p/>
	 * 5)header中还没有设置Content-Disposition的值.
	 * @param request
	 * @param response
	 * @param path 请求的资源的路径信息，格式如/demo/test.htm
	 * @return
	 */
	protected boolean useGzip(HttpServletRequest request,
			HttpServletResponse response, String path) {
		// 如果回应的header中已经设置Content-Encoding，则返回false
		if (response.containsHeader("Content-Encoding") || response.containsHeader("Content-Disposition")) {
			return false;
		}

		// 是否强制不使用压缩
		if ("false".equals(request.getParameter("gzip"))) {
			return false;
		}

		if (minThreshold > 0) {
			path = path.toLowerCase();

			//对图片资源自动强制不使用压缩
			if (path.endsWith(".gif") || path.endsWith(".png")
					|| path.endsWith(".jpg")|| path.endsWith(".jpeg")) {
				return false;
			}

			//只有浏览器支持gzip才使用压缩
			if(!browserSupportGzip(request)){
				return false;
			}
		}

		return true;
	}

	/**判断浏览器是否支持gzip
	 * @param request
	 * @return
	 */
	protected boolean browserSupportGzip(HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Enumeration e = request.getHeaders("Accept-Encoding");
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (name.indexOf("gzip") != -1) {
				return true;
			}
		}
		return false;
	}
	
    /**
     * 设置header的 "<tt>Content-Encoding</tt>" 值为"<tt>gzip</tt>"
     * <p/>
     * 通过(&lt;jsp:include&gt;)引入的文件，该方法将返回false。
     *
     * @return 设置成功返回true，否则返回false
     */
	protected boolean setContentEncodingGZip(HttpServletResponse response) {
        response.addHeader("Content-Encoding", "gzip");
        response.addHeader("Vary", "Accept-Encoding");
        return response.containsHeader("Content-Encoding");
    }
}
