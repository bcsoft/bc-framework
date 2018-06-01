package cn.bc.orm.jpa;

import cn.bc.core.EnumWithValue;

import javax.persistence.AttributeConverter;

/**
 * Enum 类型持久化值转换器基类
 *
 * @author dragon 2016-01-04
 */
public abstract class EnumWithValueConverter<E extends EnumWithValue> implements AttributeConverter<E, Integer> {
  @Override
  public Integer convertToDatabaseColumn(E attribute) {
    return attribute == null ? null : attribute.value();
  }
}