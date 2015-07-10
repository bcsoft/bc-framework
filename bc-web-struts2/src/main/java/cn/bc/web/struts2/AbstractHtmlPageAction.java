/**
 *
 */
package cn.bc.web.struts2;

import cn.bc.Context;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.Toolbar;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.RequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Html页面抽象Action
 *
 * @author dragon
 */
public abstract class AbstractHtmlPageAction extends ActionSupport implements
		SessionAware, RequestAware {
	private static final long serialVersionUID = 1L;
	protected Log logger = LogFactory.getLog(this.getClass());

	/**
	 * 成功生成Html页面后所返回的视图名
	 */
	public static final String PAGE = "page";

	/**
	 * Ajax成功处理后返回Json信息对应的视图名
	 */
	public static final String JSON = "json";

	public String json; // json页面
	public Component html; // html页面
	protected Map<String, Object> session;
	protected Map<String, Object> request;

	/**
	 * 获取 struts 的 XML 配置文件中此 Action 配置的 namespace 的值相对于web应用上下文路径的相对路径
	 * <p>如 &lt;package name="XXX" extends="XXX" namespace="/bc/actor"&gt;...&lt;/package&gt;
	 * 则此方法返回 "bc/actor" 而不是 "/bc/actor"</p>
	 * @return 包路径
	 */
	public String getActionNamespace() {
		String ns = ServletActionContext.getActionMapping().getNamespace();
		if(ns.startsWith("/")) return ns.substring(1);
		else return ns;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public void setRequest(Map<String, Object> request) {
		this.request = request;
	}

	public Context getContext() {
		return (Context) this.session.get(Context.KEY);
	}

	// == 子类必须复写的方法

	/**
	 * 判断表单是否只读的方法，需要权限控制时由基类复写
	 *
	 * @return
	 */
	public boolean isReadonly() {
		return false;
	}

	/**
	 * 页面的标题
	 */
	protected abstract String getHtmlPageTitle();

	/**
	 * 页面的工具条
	 */
	protected abstract Toolbar getHtmlPageToolbar();

	/**
	 * 访问页面的命名空间，默认与 struts 配置文件中的 namespace 值保持一致
	 */
	protected String getHtmlPageNamespace(){
		return getActionNamespace();
	}

	@Override
	public String execute() throws Exception {
		// 构建页面的html
		this.html = buildHtmlPage();

		// 返回
		return PAGE;
	}

	// == 默认实现的方法

	/**
	 * 构建页面的html代码
	 */
	protected HtmlPage buildHtmlPage() {
		HtmlPage htmlPage = new HtmlPage();

		// 设置页面参数
		htmlPage.setNamespace(getHtmlPageNamespace())
				.setTitle(this.getHtmlPageTitle())
				.setInitMethod(getHtmlPageInitMethod())
				.setOption(getHtmlPageOption().toString()).setBeautiful(true)
				.addClazz("bc-page");

		// 引用额外的js、css文件
		htmlPage.addJs(getHtmlPageJs());// 兼容旧的代码
		List<String> container = new ArrayList<>();
		addJsCss(container);
		if (!container.isEmpty()) htmlPage.setAttr("data-js", new JSONArray(container).toString());

		// 附加工具条
		htmlPage.addChild(getHtmlPageToolbar());

		return htmlPage;
	}

	/**
	 * 页面配置参数
	 */
	protected PageOption getHtmlPageOption() {
		return new PageOption().setMinWidth(250).setMinHeight(120)
				.setModal(false).setMinimizable(true).setMaximizable(true);
	}

	/**
	 * 页面加载后调用的js初始化方法
	 */
	protected String getHtmlPageInitMethod() {
		return null;
	}

	/**
	 * 向页面添加额外的js、css文件
	 * <p>调用此方法生成的data-js属性为json数组格式，页面将使用requireJs加载这些js、css文件</p>
	 *
	 * @param container 已初始化好的容器
	 */
	protected void addJsCss(List<String> container) {
		// Do nothing
	}

	/**
	 * 页面需要另外加载的js、css文件，逗号连接多个文件
	 * <p>此方式为旧的加载js、css文件的方法，新模块不要继续使用</p>
	 *
	 * @deprecated use addJsCss(List<String>)
	 */
	protected String getHtmlPageJs() {
		Collection<String> jscss = new ArrayList<String>();
		addHtmlPageJsCss(jscss, getContextPath());
		return StringUtils.collectionToCommaDelimitedString(jscss);
	}

	/**
	 * 向页面添加额外的js文件
	 *
	 * @param jscss       已初始化好的集合
	 * @param contextPath 上下文路径
	 * @deprecated use addJsCss(List<String>)
	 */
	protected void addHtmlPageJsCss(Collection<String> jscss, String contextPath) {
		// Do nothing
	}

	/**
	 * 获取访问该ation的上下文路径
	 */
	protected String getContextPath() {
		return ServletActionContext.getRequest().getContextPath();
	}
}
