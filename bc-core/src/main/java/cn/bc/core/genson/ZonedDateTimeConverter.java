package cn.bc.core.genson;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The ISO-like date-time formatter that formats or parses a date-time with offset and zone, such as '2011-12-03T10:15:30+01:00[Europe/Paris]'.
 *
 * @author dragon 2016-10-25
 */
public class ZonedDateTimeConverter implements Converter<ZonedDateTime> {
	private final static DateTimeFormatter defaultFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	protected final DateTimeFormatter formatter;

	public ZonedDateTimeConverter() {
		this.formatter = defaultFormatter;
	}

	public ZonedDateTimeConverter(DateTimeFormatter formatter) {
		this.formatter = formatter == null ? defaultFormatter : formatter;
	}

	@Override
	public void serialize(ZonedDateTime object, ObjectWriter writer, Context ctx) throws Exception {
		writer.writeString(object.format(formatter));
	}

	@Override
	public ZonedDateTime deserialize(ObjectReader reader, Context ctx) throws Exception {
		return ZonedDateTime.parse(reader.valueAsString(), formatter);
	}
}
