/**
 * 
 */
package cn.bc.core.exception;

/**
 * 对象不存在异常
 * 
 * @author dragon
 * 
 */
public class NotExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotExistsException() {
		super();
	}

	public NotExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotExistsException(String message) {
		super(message);
	}

	public NotExistsException(Throwable cause) {
		super(cause);
	}
}
