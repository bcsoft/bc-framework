package cn.bc.web.ws.dotnet;

import cn.bc.web.formater.Formater;

/**
 * DataSet的列
 *
 * @author dragon
 */
public class Column {
  private String name;// 列名
  private Object type;// 值类型
  private Formater<?> formater;// 单元格值的格式化处理器

  public Column(String name, Object type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public Column setName(String name) {
    this.name = name;
    return this;
  }

  public Object getType() {
    return type;
  }

  public Column setType(Object type) {
    this.type = type;
    return this;
  }

  public Formater<?> getFormater() {
    return formater;
  }

  public void setFormater(Formater<?> formater) {
    this.formater = formater;
  }

  @Override
  public String toString() {
    return "{type:\"" + this.getType() + "\",name:\"" + this.getName()
      + "\"}";
  }
}
