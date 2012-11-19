package cn.bc.web.ui.html.tree;

import cn.bc.web.ui.json.Json;

/**
 * 树(根节点)
 * 
 * @author dragon
 * 
 */
public class Tree extends TreeNode {
	private String url; // 获取树数据的url
	private Json cfg; // 配置参数
	private boolean showRoot; // 是否显示根节点
	private boolean selectable; // 鼠标是否可以选择树的文字

	public Tree() {
		this(null, null);
	}

	/**
	 * @param rootNodeId
	 *            根节点的节点ID值
	 * @param rootNodeLabel
	 *            根节点名称
	 */
	public Tree(String rootNodeId, String rootNodeLabel) {
		super(rootNodeId, rootNodeLabel);
		this.setOpen(true);
		this.setLeaf(false);
	}

	@Override
	public String getTag() {
		return "div";
	}
	
	public String getUrl() {
		return url;
	}

	public Tree setUrl(String url) {
		this.url = url;
		return this;
	}

	public Json getCfg() {
		return cfg;
	}

	public Tree setCfg(Json cfg) {
		this.cfg = cfg;
		return this;
	}

	public boolean isShowRoot() {
		return showRoot;
	}

	public Tree setShowRoot(boolean showRoot) {
		this.showRoot = showRoot;
		return this;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public Tree setSelectable(boolean selectable) {
		this.selectable = selectable;
		return this;
	}

	@Override
	protected void init() {
		this.addClazz("bc-tree ui-widget-content treeNode");
	}

	@Override
	public StringBuffer render(StringBuffer main) {
		// 树属性的渲染
		this.setAttr("data-url", this.getUrl());
		if (this.getCfg() != null)
			this.setAttr("data-cfg", this.getCfg().toString());
		if (!this.isSelectable())
			this.addClazz("unselectable");

		// 执行基类的渲染
		return super.render(main);
	}

	@Override
	protected void renderItem(StringBuffer main) {
		if (isShowRoot()) {
			super.renderItem(main);
		}
	}

	@Override
	protected void renderClass() {
		super.renderClass();
	}

	@Override
	protected void renderSubNodes(StringBuffer main) {
		super.renderSubNodes(main);
	}
}
