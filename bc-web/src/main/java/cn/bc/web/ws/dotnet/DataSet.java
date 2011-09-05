/**
 * 
 */
package cn.bc.web.ws.dotnet;

import java.util.ArrayList;
import java.util.List;

/**
 * DotNet的DataSet简易封装
 * 
 * @author dragon
 * 
 */
public class DataSet {
	private List<Column> columns;// 列
	private List<Row> rows;// 行
	private String msg;// 其他信息，如错误的返回信息

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public DataSet setColumns(List<Column> columns) {
		this.columns = columns;
		return this;
	}

	public List<Row> getRows() {
		return rows;
	}

	public DataSet setRows(List<Row> rows) {
		this.rows = rows;
		return this;
	}

	public DataSet addColumn(Column column) {
		if (this.columns == null)
			this.columns = new ArrayList<Column>();
		this.columns.add(column);
		return this;
	}

	public Column getColumn(String columnName) {
		if (this.columns == null)
			return null;
		for (Column c : this.columns) {
			if (c.getName().equals(columnName))
				return c;
		}
		return null;
	}

	public DataSet addRow(Row row) {
		if (this.rows == null)
			this.rows = new ArrayList<Row>();
		this.rows.add(row);
		return this;
	}

	@Override
	public String toString() {
		StringBuffer t = new StringBuffer();

		// 消息
		if (this.getMsg() != null) {
			t.append("msg:\"" + this.getMsg() + "\"\r\n");
		} else {
			t.append("msg:null\r\n");
		}

		// 列
		if (this.columns != null) {
			int i = 0;
			t.append("columns(" + this.columns.size() + "):\r\n");
			for (Column column : columns) {
				t.append((i > 0 ? "\r\n  " : "  ") + column.toString());
				i++;
			}
		} else {
			t.append("columns:(null)\r\n");
		}

		// 行
		t.append("\r\n");
		if (this.rows != null) {
			int i = 0;
			t.append("rows(" + this.rows.size() + "):\r\n");
			for (Row row : rows) {
				t.append((i > 0 ? "\r\n  " : "  ") + row.toString());
				i++;
			}
		} else {
			t.append("rows:(null)\r\n");
		}
		return t.toString();
	}
}
