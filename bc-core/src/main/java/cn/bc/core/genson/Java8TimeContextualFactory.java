package cn.bc.core.genson;

import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.annotation.JsonDateFormat;
import com.owlike.genson.convert.ContextualFactory;
import com.owlike.genson.reflect.BeanProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * genson 增加对 java8 日期时间类型的支持
 *
 * @author dragon 2016-10-25
 * @see GensonContextResolver
 */
public class Java8TimeContextualFactory implements ContextualFactory {
	@Override
	public Converter create(BeanProperty property, Genson genson) {
		JsonDateFormat ann = property.getAnnotation(JsonDateFormat.class);
		if (LocalDate.class.isAssignableFrom(property.getRawClass())) {
			return new LocalDateConverter(createFormatter(ann));
		} else if (LocalDateTime.class.isAssignableFrom(property.getRawClass())) {
			return new LocalDateTimeConverter(createFormatter(ann));
		} else if (LocalTime.class.isAssignableFrom(property.getRawClass())) {
			return new LocalTimeConverter(createFormatter(ann));
		} else if (java.time.ZonedDateTime.class.isAssignableFrom(property.getRawClass())) {
			return new ZonedDateTimeConverter(createFormatter(ann));
		}
		return null;
	}

	private DateTimeFormatter createFormatter(JsonDateFormat ann) {
		if (ann == null) return null;
		if (ann.value() == null || ann.value().isEmpty()) return null;
		else {
			Locale locale = (ann.lang() == null || ann.lang().isEmpty()) ? null : new Locale(ann.lang());
			if (locale == null) return DateTimeFormatter.ofPattern(ann.value());
			else return DateTimeFormatter.ofPattern(ann.value()).withLocale(locale);
		}
	}
}
