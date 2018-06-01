/**
 *
 */
package cn.bc.web.ws.dotnet;

/**
 * DataSet行的单元格
 *
 * @author dragon
 */
public class Cell {
  private String key;// 标识
  private Object value;// 值

  public Cell(String key, Object value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }
}
