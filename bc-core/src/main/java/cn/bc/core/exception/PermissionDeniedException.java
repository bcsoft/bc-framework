/**
 * 
 */
package cn.bc.core.exception;

/**
 * 执行没有权限的操作引发的异常
 * 
 * @author dragon
 * 
 */
public class PermissionDeniedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PermissionDeniedException() {
		super();
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
	}
}
