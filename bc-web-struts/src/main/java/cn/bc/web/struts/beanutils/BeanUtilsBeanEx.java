package cn.bc.web.struts.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.beanutils.ContextClassLoaderLocal;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.expression.Resolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * test
 * 扩展BeanUtilsBean使copyProperty方法当value参数为DynaBean时
 * 使用BeanUtilsEx.copyProperties(oldValue, value)方法拷贝属性值，
 * 这让就可以让就可让Domain的嵌套属性支持在ActionForm中也可以使用 LazyDynaBean来定义
 * 
 * @author dragon
 * @deprecated
 */
public class BeanUtilsBeanEx extends org.apache.commons.beanutils.BeanUtilsBean {
	private Log log = LogFactory.getLog(BeanUtilsBeanEx.class);
	private static final ContextClassLoaderLocal BEANS_BY_CLASSLOADER = new ContextClassLoaderLocal() {
		protected Object initialValue() {
			return new BeanUtilsBeanEx();
		}
	};

	public static BeanUtilsBeanEx getInstance() {
		return (BeanUtilsBeanEx) BEANS_BY_CLASSLOADER.get();
	}

	public static void setInstance(BeanUtilsBeanEx newInstance) {
		BEANS_BY_CLASSLOADER.set(newInstance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		// Trace logging (if enabled)
		if (log.isTraceEnabled()) {
			StringBuffer sb = new StringBuffer("  copyProperty(");
			sb.append(bean);
			sb.append(", ");
			sb.append(name);
			sb.append(", ");
			if (value == null) {
				sb.append("<NULL>");
			} else if (value instanceof String) {
				sb.append((String) value);
			} else if (value instanceof String[]) {
				String[] values = (String[]) value;
				sb.append('[');
				for (int i = 0; i < values.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(values[i]);
				}
				sb.append(']');
			} else {
				sb.append(value.toString());
			}
			sb.append(')');
			log.trace(sb.toString());
		}

		// Resolve any nested expression to get the actual target bean
		Object target = bean;
		Resolver resolver = getPropertyUtils().getResolver();
		while (resolver.hasNested(name)) {
			try {
				target = getPropertyUtils().getProperty(target,
						resolver.next(name));
				name = resolver.remove(name);
			} catch (NoSuchMethodException e) {
				return; // Skip this property setter
			}
		}
		if (log.isTraceEnabled()) {
			log.trace("    Target bean = " + target);
			log.trace("    Target name = " + name);
		}

		// Declare local variables we will require
		String propName = resolver.getProperty(name); // Simple name of target
		// property
		Class type = null; // Java type of target property
		int index = resolver.getIndex(name); // Indexed subscript value (if any)
		String key = resolver.getKey(name); // Mapped key value (if any)

		// Calculate the target property type
		if (target instanceof DynaBean) {
			DynaClass dynaClass = ((DynaBean) target).getDynaClass();
			DynaProperty dynaProperty = dynaClass.getDynaProperty(propName);
			if (dynaProperty == null) {
				return; // Skip this property setter
			}
			type = dynaProperty.getType();
		} else {
			PropertyDescriptor descriptor = null;
			try {
				descriptor = getPropertyUtils().getPropertyDescriptor(target,
						name);
				if (descriptor == null) {
					return; // Skip this property setter
				}
			} catch (NoSuchMethodException e) {
				return; // Skip this property setter
			}
			type = descriptor.getPropertyType();
			if (type == null) {
				// Most likely an indexed setter on a POJB only
				if (log.isTraceEnabled()) {
					log.trace("    target type for property '" + propName
							+ "' is null, so skipping ths setter");
				}
				return;
			}
		}
		if (log.isTraceEnabled()) {
			log.trace("    target propName=" + propName + ", type=" + type
					+ ", index=" + index + ", key=" + key);
		}

		// Convert the specified value to the required type and store it
		if (index >= 0) { // Destination must be indexed
			value = convert(value, type.getComponentType());
			try {
				getPropertyUtils().setIndexedProperty(target, propName, index,
						value);
			} catch (NoSuchMethodException e) {
				throw new InvocationTargetException(e, "Cannot set " + propName);
			}
		} else if (key != null) { // Destination must be mapped
			// Maps do not know what the preferred data type is,
			// so perform no conversions at all
			// FIXME - should we create or support a TypedMap?
			try {
				getPropertyUtils().setMappedProperty(target, propName, key,
						value);
			} catch (NoSuchMethodException e) {
				throw new InvocationTargetException(e, "Cannot set " + propName);
			}
		} else { // 这里的代码就是被修改过的，上面的都是BeanUtilsBean原来的代码
			if (value instanceof DynaBean || isClassObject(value)) {
				try {
					PropertyDescriptor descriptor = getPropertyUtils()
							.getPropertyDescriptor(bean, name);
					if (descriptor == null) {
						throw new NoSuchMethodException("Unknown property '"
								+ name + "' on class '" + bean.getClass() + "'");
					}
					Method readMethod = descriptor.getReadMethod();
					if (readMethod == null) {
						throw new NoSuchMethodException("Property '" + name
								+ "' has no getter method in class '"
								+ bean.getClass() + "'");
					}

					// Call the property getter and return the value
					Object oldValue = readMethod.invoke(bean);
					if (oldValue == null) {
						oldValue = descriptor.getPropertyType().newInstance();// 初始化嵌套属性
					}
					BeanUtilsEx.copyProperties(oldValue, value);// 这里才是重点
				} catch (NoSuchMethodException e) {
					throw new InvocationTargetException(e, "Cannot set " + name
							+ " of " + bean.getClass());
				} catch (InstantiationException e) {
					throw new InvocationTargetException(e, "Cannot Instance "
							+ name + " of " + bean.getClass());
				}
			} else {// 下面是BeanUtilsBean原来的代码
				// Destination must be simple
				value = convert(value, type);
				try {
					getPropertyUtils().setSimpleProperty(target, propName,
							value);
				} catch (NoSuchMethodException e) {
					throw new InvocationTargetException(e, "Cannot set "
							+ propName);
				}
			}
		}
	}

	private boolean isClassObject(Object value) {
		boolean ok = !(value.getClass().isPrimitive()
				|| String.class == value.getClass() || Number.class
				.isAssignableFrom(value.getClass()));// false;
		//System.out.println(ok + ";" + value + ";" + value.getClass());
		return ok;
		// return !(value.getClass().isPrimitive()
		// || value.getClass().isArray()
		// || value instanceof String || value instanceof Collection);
	}
}
