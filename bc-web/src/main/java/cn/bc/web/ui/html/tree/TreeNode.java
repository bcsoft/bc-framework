package cn.bc.web.ui.html.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.bc.web.ui.html.Table;
import cn.bc.web.ui.html.Tbody;
import cn.bc.web.ui.html.Td;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Tr;

/**
 * 树节点
 * 
 * @author dragon
 * 
 */
public class TreeNode extends Td {
	private String nodeId; // 节点ID
	private String label; // 节点名称
	private String icon; // 叶子节点的图标样式，文件夹节点暂不支持
	private boolean leaf; // 是否是叶子节点
	private boolean open; // 文件夹节点是否处于展开状态
	private boolean borderRadius; // 是否使用圆角边框
	private TreeNode parent; // 所隶属的上级节点，为空代表是根节点
	private List<TreeNode> subNodes;// 子节点

	/**
	 * @param nodeId
	 *            节点的ID值
	 * @param label
	 *            节点名称
	 * @param leaf
	 *            是否是叶子节点
	 */
	public TreeNode(String nodeId, String label, boolean leaf) {
		this.nodeId = nodeId;
		this.label = label;
		this.leaf = leaf;
	}

	/**
	 * @param nodeId
	 *            节点的ID值
	 * @param label
	 *            节点名称
	 */
	public TreeNode(String nodeId, String label) {
		this.nodeId = nodeId;
		this.label = label;
	}

	public int getLevel() {
		if (this.getParent() == null) {
			return 0;
		} else {
			return this.getParent().getLevel() + 1;
		}
	}

	public String getNodeId() {
		return nodeId;
	}

	public TreeNode setNodeId(String nodeId) {
		this.nodeId = nodeId;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public TreeNode setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getIcon() {
		return icon;
	}

	public TreeNode setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	public boolean isBorderRadius() {
		return borderRadius;
	}

	public TreeNode setBorderRadius(boolean borderRadius) {
		this.borderRadius = borderRadius;
		return this;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public TreeNode setLeaf(boolean leaf) {
		this.leaf = leaf;
		if (leaf)
			this.open = false;
		return this;
	}

	public boolean isOpen() {
		return open;
	}

	public TreeNode setOpen(boolean open) {
		this.open = open;
		return this;
	}

	public TreeNode getParent() {
		return parent;
	}

	public TreeNode setParent(TreeNode parent) {
		this.parent = parent;
		return this;
	}

	public List<TreeNode> getSubNodes() {
		return subNodes;
	}

	public TreeNode addSubNode(TreeNode subNode) {
		if (this.subNodes == null)
			this.subNodes = new ArrayList<TreeNode>();

		if (subNode == null)
			return this;

		subNode.setParent(this);
		this.subNodes.add(subNode);
		this.leaf = false;
		return this;

	}

	@Override
	protected void init() {
		this.addClazz("treeNode");
	}

	@Override
	public StringBuffer render(StringBuffer main) {
		// 样式控制
		renderClass();

		// 生成item元素
		renderItem(main);

		// 生成子节点列表元素
		renderSubNodes(main);

		// 执行基类的渲染
		return super.render(main);
	}

	/**
	 * 样式控制
	 */
	protected void renderClass() {
		if (isLeaf()) {
			this.addClazz("leaf");// 叶子节点
		} else {
			if (isOpen()) {
				this.addClazz("folder open");// 展开的文件夹
			} else {
				this.addClazz("folder collapsed");// 折叠的文件夹
			}
		}
		
		// 子节点的嵌套级别
		int level = this.getLevel();
		this.setAttr("data-level", String.valueOf(level));
	}

	/**
	 * 渲染节点的子节点列表
	 */
	protected void renderSubNodes(StringBuffer main) {
		Table table = buildSubNodes(getSubNodes());
		if (table != null) {
			this.addChild(table);
		}
	}

	/**
	 * 构建子节点列表DOM
	 */
	public static Table buildSubNodes(Collection<TreeNode> subNodes) {
		if (subNodes != null && !subNodes.isEmpty()) {
			Table table = new Table();
			table.addClazz("nodes");
			Tbody tbody = new Tbody();
			table.addChild(tbody);
			Tr tr;
			for (TreeNode n : subNodes) {
				tr = new Tr();
				tr.addChild(n);
				tbody.addChild(tr);
			}
			return table;
		} else {
			return null;
		}
	}

	/**
	 * 渲染节点的item元素
	 */
	protected void renderItem(StringBuffer main) {
		Text item = new Text("<div data-id=\""
				+ getNodeId()
				+ "\" class=\"item ui-widget-content ui-state-normal"
				+ (isBorderRadius() ? " ui-corner-all" : "")
				+ "\">"
				+ "<span class=\"nav-icon ui-icon"
				+ (isLeaf() ? "" : (isOpen() ? " ui-icon-triangle-1-se"
						: " ui-icon-triangle-1-e"))
				+ "\"></span>"
				+ "<span class=\"type-icon ui-icon "
				+ (getIcon() != null ? getIcon()
						: (isLeaf() ? "ui-icon-document"
								: (isOpen() ? "ui-icon-folder-open"
										: "ui-icon-folder-collapsed")))
				+ "\"></span>" + "<span class=\"text\">" + this.getLabel()
				+ "</span></div>");
		this.addChild(item);
	}
}
