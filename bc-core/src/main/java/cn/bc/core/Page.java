/**
 * 
 */
package cn.bc.core;

import java.util.List;

/**
 * 分页数据的封装
 * 
 * @author dragon
 * 
 */
public class Page<T extends Object> {
	protected int pageNo;
	protected int pageSize;
	protected int totalCount;
	//protected int pageCount;
	protected List<T> data;
	
	public Page() {
		this.pageNo = 1;
	}

	/**
	 * 构造一页数据
	 * 
	 * @param pageNo
	 *            当前页码(>=1)
	 * @param pageSize
	 *            每页容量(>=1)
	 * @param totalCount
	 *            总条目数(>=0)
	 * @param data
	 *            页包含的数据
	 */
	public Page(int pageNo, int pageSize, int totalCount, List<T> data) {
		this();
		this.pageNo = pageNo < 1 ? 1 : pageNo;
		this.pageSize = pageSize < 1 ? 1 : pageSize;
		this.totalCount = totalCount < 0 ? 0 : totalCount;
		this.data = data;
		//this.pageCount = getPageCount();
	}

	/**
	 * @return 当前页码，从1开始
	 */
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return 第一条数据的全局索引号，从0开始
	 */
	public int getFirstResult() {
		return (pageNo - 1) * pageSize;
	}

	/**
	 * 计算某页第一条数据的全局索引号，从0开始
	 * @param pageNo 页码
	 * @param pageSize 也的容量限制
	 * @return 某页第一条数据的全局索引号
	 */
	public static int getFirstResult(int pageNo, int pageSize) {
		pageNo = pageNo < 1 ? 1 : pageNo;
		pageSize = pageSize < 1 ? 1 : pageSize;
		return (pageNo - 1) * pageSize;
	}

	/**
	 * @return 每页数据的最大容量
	 */
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return 总条目数
	 */
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return 该页包含的数据
	 */
	public List<T> getData() {
		return data;
	}
	public void setData(List<T> list) {
		this.data = list;
	}

	/**
	 * @return 总页数
	 */
	public int getPageCount() {
		return (totalCount % pageSize > 0) ? totalCount / pageSize + 1
				: totalCount / pageSize;
	}
}
