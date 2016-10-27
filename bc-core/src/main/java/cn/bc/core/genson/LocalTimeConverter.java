package cn.bc.core.genson;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 时间转换：默认转换为 HH:mm:ss 格式，可以通过 @JsonDateFormat("HH:mm") 配置为其它格式
 *
 * @author dragon 2016-10-25
 */
public class LocalTimeConverter implements Converter<LocalTime> {
	private final static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
	protected final DateTimeFormatter formatter;

	public LocalTimeConverter() {
		this.formatter = defaultFormatter;
	}

	public LocalTimeConverter(DateTimeFormatter formatter) {
		this.formatter = formatter == null ? defaultFormatter : formatter;
	}

	@Override
	public void serialize(LocalTime object, ObjectWriter writer, Context ctx) throws Exception {
		writer.writeString(object.format(formatter));
	}

	@Override
	public LocalTime deserialize(ObjectReader reader, Context ctx) throws Exception {
		return LocalTime.parse(reader.valueAsString(), formatter);
	}
}
