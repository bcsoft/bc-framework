package cn.bc.core.genson;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxb.JAXBBundle;

import javax.annotation.Priority;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * 自定义 genson 实例，增加对 java8 日期时间类型的支持
 *
 * @author dragon 2016-10-25
 */
@Provider
@Priority(2000 - 100 - 1)
public class GensonContextResolver implements ContextResolver<Genson> {
	@Override
	public Genson getContext(Class<?> type) {
		return new GensonBuilder()
				// copy from com.owlike.genson.ext.jaxrs.GensonJaxRSFeature._defaultGenson
				.withBundle(new JAXBBundle())
				.useConstructorWithArguments(true)

				.useDateAsTimestamp(false)

				// java8 time support
				.withContextualFactory(new Java8TimeContextualFactory())
				.create();
	}
}