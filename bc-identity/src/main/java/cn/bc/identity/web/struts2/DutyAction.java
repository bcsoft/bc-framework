/**
 * 
 */
package cn.bc.identity.web.struts2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.Page;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.impl.LikeCondition;
import cn.bc.core.query.condition.impl.OrCondition;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.domain.Duty;
import cn.bc.identity.service.DutyService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridExporter;
import cn.bc.web.ui.html.grid.IdColumn;
import cn.bc.web.ui.html.grid.TextColumn;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 职务Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class DutyAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(DutyAction.class);

	private DutyService dutyService;
	private IdGeneratorService idGeneratorService;
	private Long id;
	private Duty e;// entity的简写
	private List<Duty> es;// entities的简写,非分页页面用
	private Page<Duty> page;// 分页页面用
	private String ids;
	private String search;// 搜索框输入的文本

	@Autowired
	public void setDutyService(DutyService dutyService) {
		this.dutyService = dutyService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public Duty getE() {
		return e;
	}

	public void setE(Duty e) {
		this.e = e;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Duty> getEs() {
		return es;
	}

	public void setEs(List<Duty> es) {
		this.es = es;
	}

	public Page<Duty> getPage() {
		return page;
	}

	public void setPage(Page<Duty> page) {
		this.page = page;
	}

	public String execute() throws Exception {
		logger.debug("do in method execute.");
		return SUCCESS;
	}

	// 新建
	public String create() throws Exception {
		e = this.dutyService.create();
		e.setCode(this.idGeneratorService.next("duty.code"));
		return "form";
	}

	// 编辑
	public String edit() throws Exception {
		e = this.dutyService.load(this.getId());
		return "form";
	}

	// 保存
	public String save() throws Exception {
		this.dutyService.save(e);
		return "saveSuccess";
	}

	// 删除
	public String delete() throws Exception {
		if (this.getId() != null) {// 删除一条
			this.dutyService.delete(this.getId());
		} else {// 删除一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = StringUtils.stringArray2LongArray(this.getIds()
						.split(","));
				this.dutyService.delete(ids);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		return "deleteSuccess";
	}

	// 获取列表视图页面----无分页
	public String list() throws Exception {
		this.es = findList();
		return "list";
	}

	protected List<Duty> findList() {
		return this.dutyService.createQuery()
				.condition(this.getSearchCondition()).list();
	}

	// 获取列表视图页面----分页
	public String paging() throws Exception {
		if (page == null)
			page = new Page<Duty>();
		if (page.getPageSize() < 1)
			page.setPageSize(Integer.parseInt(getText("app.pageSize")));

		// 构建查询条件并执行查询
		page = findPage();
		this.es = page.getData();
		return "paging";
	}

	protected Page<Duty> findPage() {
		return this.dutyService.createQuery()
				.condition(this.getSearchCondition())
				.page(page.getPageNo(), page.getPageSize());
	}

	/**
	 * 构建查询条件
	 * 
	 * @return
	 */
	protected Condition getSearchCondition() {
		if (this.search != null && this.search.length() > 0) {
			return new OrCondition()
					.add(new LikeCondition("code", this.search)).add(
							new LikeCondition("name", this.search));
		} else {
			return null;
		}
	}

	// 仅获取表格的数据信息部分
	public String data() throws Exception {
		if (this.page != null) {// 分页的处理
			this.page = this.dutyService.createQuery()
					.condition(this.getSearchCondition())
					.page(page.getPageNo(), page.getPageSize());
			this.es = page.getData();
		} else {// 非分页的处理
			this.es = this.dutyService.createQuery()
					.condition(this.getSearchCondition()).list();
		}
		return "data";
	}

	// 导出视图数据需要的变量
	public boolean exporting;// 标记当前处于导出状态
	public String exportKeys;// 要导出列的标识，用逗号连接多个
	public String fileName;// 下载的文件名
	public InputStream inputStream;// 下载文件对应的流
	public String contentType = "application/vnd.ms-excel";// 下载文件类型定义
	public int bufferSize = 4096;
	public String inputName = "inputStream";// 文件输出流定义
	public String contentDisposition;// 下载文件处理方法

	// 导出表格的数据为excel文件
	public String export() throws Exception {
		// 确定下载文件的名称(解决跨浏览器中文文件名乱码问题)
		if (fileName == null || fileName.length() == 0)
			fileName = getText("export.default.fileName") + getText("duty");// 默认的文件名
		String title = fileName;
		fileName = WebUtils.encodeFileName(ServletActionContext.getRequest(),
				fileName);

		// 确定下载文件处理方法
		// contentDisposition = "inline;filename=\"" +fileName
		// +".xls\"";//在页面中打开
		contentDisposition = "attachment;filename=\"" + fileName + ".xls\"";// 以附件方式下载

		// TODO 根据导出条件构建Grid
		if (this.page != null) {// 分页的处理
			this.page = this.findPage();
			this.es = this.page.getData();
		} else {// 非分页的处理
			this.es = this.findList();
		}

		// 导出数据到Excel
		GridExporter exporter = new GridExporter();
		exporter.setColumns(this.buildGridColumns()).setTitle(title)
				.setData(this.es).setIdLabel(getText("label.idLabel"));
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		exporter.exportTo(out);
		this.inputStream = new ByteArrayInputStream(out.toByteArray());

		return "export";

		// Content-Type:
		// 163下载.doc：application/msword
		// 163下载.xls：application/vnd.ms-excel
		// 163下载.txt：text/plain
		// 163下载.html：text/html
		// 163下载.rar|.reg：application/octet-stream
		// application/x-msdownload
	}

	protected List<Column> buildGridColumns() {
		List<Column> columns = new ArrayList<Column>();
		columns.add(IdColumn.DEFAULT());
		if (this.useColumn("code"))
			columns.add(new TextColumn("code", getText("duty.code"))
					.setSortable(true));
		if (this.useColumn("name"))
			columns.add(new TextColumn("name", getText("duty.name"), 100)
					.setSortable(true));
		return columns;
	}

	/**
	 * 判断指定的列是否应该添加
	 * 
	 * @param key
	 * @return
	 */
	protected boolean useColumn(String key) {
		return !this.exporting
				|| (this.exportKeys == null || this.exportKeys.length() == 0 || this.exportKeys
						.indexOf(key) != -1);
	}
}
