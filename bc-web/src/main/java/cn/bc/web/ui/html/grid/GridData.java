package cn.bc.web.ui.html.grid;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import cn.bc.web.formater.Formater;
import cn.bc.web.ui.Component;
import cn.bc.web.ui.html.Div;
import cn.bc.web.ui.html.Span;
import cn.bc.web.ui.html.Table;
import cn.bc.web.ui.html.Td;
import cn.bc.web.ui.html.Text;
import cn.bc.web.ui.html.Tr;

/**
 * 表格数据部分的html组件
 * 
 * @author dragon
 * 
 */
public class GridData extends Div {
	private static final Log logger = LogFactory.getLog(GridData.class);
	private List<? extends Object> data;
	private List<Column> columns = new ArrayList<Column>();
	private int pageNo;
	private int pageCount;
	// 行的显示信息表达式
	private String rowLabelExpression;
	private ExpressionParser parser;

	public String getValue(Object obj, String expression, Formater formater) {
		return getValue(obj, expression, parser, formater);
	}

	/**
	 * 获取对象指定表达式计算值的字符串表示
	 * 
	 * @param obj
	 * @param expression
	 * @param parser
	 * @return
	 */
	public static String getValue(Object obj, String expression,
			ExpressionParser parser, Formater formater) {
		if (parser == null)
			parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(expression);
		EvaluationContext context = new StandardEvaluationContext(obj);
		try {
			Object objValue = exp.getValue(context);
			String value;
			if (formater != null)
				value = formater.format(objValue);
			else
				value = (objValue != null ? objValue.toString() : "");
			return value != null ? value : "";
		} catch (EvaluationException e) {
			logger.warn(e.getMessage());
			return "";
		}
	}

	public GridData() {
		this.addClazz("data");
	}

	public GridData(int pageNo, int pageCount) {
		this();

		this.pageNo = pageNo;
		this.pageCount = pageCount;
	}

	public ExpressionParser getParser() {
		return parser;
	}

	public GridData setParser(ExpressionParser parser) {
		this.parser = parser;
		return this;
	}

	public String getRowLabelExpression() {
		return rowLabelExpression;
	}

	public GridData setRowLabelExpression(String rowLabelExpression) {
		this.rowLabelExpression = rowLabelExpression;
		return this;
	}

	public List<? extends Object> getData() {
		return data;
	}

	public GridData setData(List<? extends Object> data) {
		this.data = data;
		return this;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public GridData setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public int getPageNo() {
		return pageNo;
	}

	public GridData setPageNo(int pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public int getPageCount() {
		return pageCount;
	}

	public GridData setPageCount(int pageCount) {
		this.pageCount = pageCount;
		return this;
	}

	public StringBuffer render(StringBuffer main) {
		buildData();

		return super.render(main);
	}

	protected void buildData() {
		// 分页信息
		if (this.pageNo > 0)
			this.setAttr("data-pageNo", this.pageNo + "");
		if (this.pageCount > 0)
			this.setAttr("data-pageCount", this.pageCount + "");

		// 总体结构
		Component left = new Div().addClazz("left");
		Component right = new Div().addClazz("right");
		this.addChild(left).addChild(right);

		// 变量定义
		Component tr, td;
		Column column;
		int rc = 0;

		// 左table
		Component leftTable = new Table().addClazz("table")
				.setAttr("cellspacing", "0").setAttr("cellpadding", "0");
		left.addChild(leftTable);
		for (Object obj : this.data) {
			// 行设置
			tr = new Tr().addClazz("ui-state-default row");
			leftTable.addChild(tr);
			if (rc % 2 == 0) {
				tr.addClazz("odd");// 奇数行
			} else {
				tr.addClazz("even");// 偶数行
			}

			// 循环添加行的单元格（第一列为id列忽略）
			column = Grid.getIDColumn(this.columns);
			td = new Td().addClazz("id");
			tr.addChild(td);

			// 单元格宽度
			if (column.getWidth() > 0) {
				td.addStyle("width", column.getWidth() + "px");
			}

			// 单元格内容的包装容器（相对定位的元素）
			// Component wrapper = new Div().addClazz("wrapper");
			// td.addChild(wrapper);

			// 单元格内容
			String rowLabel;
			if (this.getName() != null) {
				rowLabel = this.getName() + " - ";
			} else {
				rowLabel = "";
			}
			td.setAttr(
					"data-name",
					rowLabel
							+ getValue(
									obj,
									getRowLabelExpression() != null ? getRowLabelExpression()
											: "id", null));// 行的标题
			td.setAttr("data-id",
					getValue(obj, column.getExpression(), column.getFormater()));// 行的id
			td.addChild(new Span().addClazz("ui-icon"));// 勾选标记符
			td.addChild(new Text(String.valueOf(rc + 1)));// 行号

			rc++;
		}

		// 右table
		Component rightTable = new Table().addClazz("table")
				.setAttr("cellspacing", "0").setAttr("cellpadding", "0");
		right.addChild(rightTable);
		int totalWidth = Grid.getDataTableWidth(this.columns);
		rightTable.addStyle("width", totalWidth + "px");
		rightTable.setAttr("originWidth", totalWidth + "");
		rc = 0;
		for (Object obj : this.data) {
			// 行设置
			tr = new Tr().addClazz("ui-state-default row");
			rightTable.addChild(tr);
			if (rc % 2 == 0) {
				tr.addClazz("odd");// 奇数行
			} else {
				tr.addClazz("even");// 偶数行
			}

			// 循环添加行的单元格（第一列为id列忽略）
			for (int i = 1; i < columns.size(); i++) {
				column = columns.get(i);
				td = new Td();
				tr.addChild(td);

				// 单元格样式
				if (i == 1) {
					td.addClazz("first");// 首列样式
				} else if (i == columns.size() - 1) {
					td.addClazz("last");// 最后列样式
				} else {
					td.addClazz("middle");// 中间列样式
				}

				// 单元格宽度
				if (column.getWidth() > 0) {
					td.addStyle("width", column.getWidth() + "px");
				}

				// 单元格内容的包装容器（相对定位的元素）
				// Component wrapper = new Div().addClazz("wrapper");
				// td.addChild(wrapper);

				// 单元格内容
				td.addChild(new Text(getValue(obj, column.getExpression(),
						column.getFormater())));
			}

			// 添加一列空列
			// tr.addChild(Grid.EMPTY_TD);

			rc++;
		}
	}
}
