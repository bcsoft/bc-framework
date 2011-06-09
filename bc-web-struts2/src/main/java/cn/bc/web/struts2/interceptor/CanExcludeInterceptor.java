/**
 * 
 */
package cn.bc.web.struts2.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import cn.bc.web.filter.CanExcludeFilter;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * 可排除某些目录或文件不作过滤处理的过滤器基类
 * 
 * <p>在&lt;interceptor&gt;中增加如下参数配置(多个配置间用逗号分隔,目录要以*结尾，文件要以*开始)：<p>
 * 
 * <pre>
 * &lt;interceptor name="yourInterceptor" class="your.package.YourInterceptor"&gt;
 *  &lt;param name="excludePaths"&gt;/demo1/*&lt;/param&gt;
 * &lt;/interceptor&gt;
 * </pre>
 * 
 * @author dragon
 * @since 2011-04-26
 */
public abstract class CanExcludeInterceptor extends AbstractInterceptor {
	private static final long serialVersionUID = 1L;
	static Log logger = LogFactory.getLog(CanExcludeFilter.class);

	/** 要排除处理的文件夹配置 */
	protected List<String> excludeDirs = new ArrayList<String>();

	/** 要排除处理的文件配置 */
	protected List<String> excludeFiles = new ArrayList<String>();

	private String excludePaths;

	public String getExcludePaths() {
		return excludePaths;
	}

	public void setExcludePaths(String excludePaths) {
		this.excludePaths = excludePaths;
		if (excludePaths != null) {
			String[] paths = StringUtils.tokenizeToStringArray(excludePaths,
					",");
			String path;
			for (int i = 0; i < paths.length; i++) {
				path = paths[i].trim();
				if (path.endsWith("*")) {
					excludeDirs.add(path.substring(0, path.length() - 1));
				} else if (path.startsWith("*")) {
					excludeFiles.add(path.substring(1));
				} else {
					String message = "excludePaths'" + path
							+ "'被忽略, 路径必须以符号*开始(文件)或结尾(文件夹)";
					logger.error(message);
				}
			}
		}
	}

	/**
	 * 判断请求的资源是否在排除处理的范围内
	 * 
	 * @param request
	 *            请求
	 * @return
	 */
	protected boolean isExcludePath(HttpServletRequest request) {
		return this.isExcludePath(WebUtils.getResourcePath(request));
	}

	/**
	 * 判断请求的资源是否在排除处理的范围内
	 * 
	 * @param path
	 *            请求的资源的路径信息，格式如/demo/test.htm
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
