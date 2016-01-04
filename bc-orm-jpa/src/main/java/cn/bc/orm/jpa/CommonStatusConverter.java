package cn.bc.orm.jpa;

import cn.bc.core.CommonStatus;

import javax.persistence.Converter;

/**
 * 通用状态的 JPA 持久化值转换器
 *
 * @author dragon 2016-01-04
 */
@Converter(autoApply = true)
public class CommonStatusConverter extends EnumWithValueConverter<CommonStatus> {
	@Override
	public CommonStatus convertToEntityAttribute(Integer dbData) {
		if (dbData == null) throw new IllegalArgumentException("unsupport CommonStatus value: null");
		return CommonStatus.valueOf(dbData);
	}
}