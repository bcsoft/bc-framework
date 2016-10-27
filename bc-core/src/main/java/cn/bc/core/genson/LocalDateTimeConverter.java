package cn.bc.core.genson;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间转换：默认转换为 yyyy-MM-dd HH:mm:ss 格式，可以通过 @JsonDateFormat("yyyy/MM/dd HH:mm:ss") 配置为其它格式
 *
 * @author dragon 2016-10-25
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime> {
	private final static DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	protected final DateTimeFormatter formatter;

	public LocalDateTimeConverter() {
		this.formatter = defaultFormatter;
	}

	public LocalDateTimeConverter(DateTimeFormatter formatter) {
		this.formatter = formatter == null ? defaultFormatter : formatter;
	}

	@Override
	public void serialize(LocalDateTime object, ObjectWriter writer, Context ctx) throws Exception {
		writer.writeString(object.format(formatter));
	}

	@Override
	public LocalDateTime deserialize(ObjectReader reader, Context ctx) throws Exception {
		return LocalDateTime.parse(reader.valueAsString(), formatter);
	}
}
