/**
 * 
 */
package cn.bc.web.ws.dotnet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DataSet的行
 * 
 * @author dragon
 * 
 */
public class Row {
	private List<Cell> cells;

	/**
	 * 获取指定的单元格
	 * 
	 * @param key
	 * @return
	 */
	public Cell getCell(String key) {
		for (Cell cell : cells) {
			if (cell.getKey().equals(key))
				return cell;
		}
		return null;
	}

	/**
	 * 获取指定的单元格的值
	 * 
	 * @param key
	 * @return
	 */
	public Object getCellValue(String key) {
		for (Cell cell : cells) {
			if (cell.getKey().equals(key))
				return cell.getValue();
		}
		return null;
	}

	/**
	 * 获取指定的单元格的字符串值
	 * 
	 * @param key
	 * @return
	 */
	public String getCellStringValue(String key) {
		Object value = this.getCellValue(key);
		return value == null ? null : value.toString();
	}

	/**
	 * 获取指定的单元格的字符串值
	 * 
	 * @param key
	 * @return
	 */
	public Calendar getCellCalendarValue(String key) {
		Object value = this.getCellValue(key);
		return value == null ? null : (Calendar) value;
	}

	public List<Cell> getCells() {
		return cells;
	}

	public Row setCells(List<Cell> cells) {
		this.cells = cells;
		return this;
	}

	public Row addCell(Cell cell) {
		if (this.cells == null)
			this.cells = new ArrayList<Cell>();
		this.cells.add(cell);
		return this;
	}

	@Override
	public String toString() {
		StringBuffer t = new StringBuffer();

		if (this.cells != null) {
			int i = 0;
			t.append("cells(" + this.cells.size() + "col){");
			for (Cell cell : cells) {
				t.append((i > 0 ? ", " : "") + cell.getKey() + ":\""
						+ convert(cell.getValue()) + "\"");
				i++;
			}
		} else {
			t.append("{null");
		}
		t.append("}");
		return t.toString();
	}

	private SimpleDateFormat df;

	private String convert(Object value) {
		if (value instanceof Calendar) {
			if (df == null)
				df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.format(((Calendar) value).getTime());
		} else if (value instanceof Date) {
			if (df == null)
				df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.format((Date) value);
		} else {
			return value != null ? value.toString() : null;
		}
	}
}
