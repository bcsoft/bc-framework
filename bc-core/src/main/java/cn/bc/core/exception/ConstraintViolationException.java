/**
 *
 */
package cn.bc.core.exception;

/**
 * 违反约束关联引发的异常
 *
 * @author dragon
 */
public class ConstraintViolationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public ConstraintViolationException() {
    super();
  }

  public ConstraintViolationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConstraintViolationException(String message) {
    super(message);
  }

  public ConstraintViolationException(Throwable cause) {
    super(cause);
  }
}
