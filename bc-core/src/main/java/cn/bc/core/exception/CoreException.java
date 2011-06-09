/**
 * 
 */
package cn.bc.core.exception;

/**
 * 异常的简单封装
 * 
 * @author dragon
 * 
 */
public class CoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CoreException() {
		super();
	}

	public CoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public CoreException(String message) {
		super(message);
	}

	public CoreException(Throwable cause) {
		super(cause);
	}
}
