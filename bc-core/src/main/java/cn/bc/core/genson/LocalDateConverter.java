package cn.bc.core.genson;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 日期转换：默认转换为 yyyy-MM-dd 格式，可以通过 @JsonDateFormat("yyyy/MM/dd") 配置为其它格式
 *
 * @author dragon 2016-10-25
 */
public class LocalDateConverter implements Converter<LocalDate> {
	private final static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
	private final DateTimeFormatter formatter;

	public LocalDateConverter() {
		this.formatter = defaultFormatter;
	}

	public LocalDateConverter(DateTimeFormatter formatter) {
		this.formatter = formatter == null ? defaultFormatter : formatter;
	}

	@Override
	public void serialize(LocalDate object, ObjectWriter writer, Context ctx) throws Exception {
		writer.writeString(object.format(formatter));
	}

	@Override
	public LocalDate deserialize(ObjectReader reader, Context ctx) throws Exception {
		return LocalDate.parse(reader.valueAsString(), formatter);
	}
}
