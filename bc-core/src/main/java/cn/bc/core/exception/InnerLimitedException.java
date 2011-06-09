/**
 * 
 */
package cn.bc.core.exception;

/**
 * 执行内置对象的受限制操作引发的异常
 * 
 * @author dragon
 * 
 */
public class InnerLimitedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InnerLimitedException() {
		super();
	}

	public InnerLimitedException(String message, Throwable cause) {
		super(message, cause);
	}

	public InnerLimitedException(String message) {
		super(message);
	}

	public InnerLimitedException(Throwable cause) {
		super(cause);
	}
}
