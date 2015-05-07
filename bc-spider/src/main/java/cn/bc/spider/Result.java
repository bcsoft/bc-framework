package cn.bc.spider;

/**
 * 异步线程返回数据结果的封装
 * 
 * @author dragon
 * 
 * @param <V>
 */
public class Result<V> {
	private boolean success;// 标识数据是否获取成功
	private V data;// 成功获取到的数据,如果success=false,则为null
	private Throwable error;// 失败时的异常信息

	public Result(boolean success, V data) {
		this.success = success;
		this.data = data;
		if(!success)
			this.error = new RuntimeException(String.valueOf(data));
	}

	public Result(Throwable e) {
		this.success = false;
		this.error = e;
	}

	public boolean isSuccess() {
		return success;
	}

	public V getData() {
		return data;
	}

	public Throwable getError() {
		return error;
	}

	public void setData(V data) {
		this.data = data;
	}
}