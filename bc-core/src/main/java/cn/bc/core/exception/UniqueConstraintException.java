/**
 *
 */
package cn.bc.core.exception;

/**
 * 唯一性约束引发的异常
 *
 * @author dragon 2015-11-05
 */
public class UniqueConstraintException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public UniqueConstraintException() {
    super();
  }

  public UniqueConstraintException(String message, Throwable cause) {
    super(message, cause);
  }

  public UniqueConstraintException(String message) {
    super(message);
  }

  public UniqueConstraintException(Throwable cause) {
    super(cause);
  }
}
