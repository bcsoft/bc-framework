package cn.bc.web.struts.beanutils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.ContextClassLoaderLocal;

/**test
 * @author dragon
 * @deprecated
 *
 */
public class BeanUtilsEx extends org.apache.commons.beanutils.BeanUtils {
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

	public static void copyProperties(Object dest, Object orig)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBeanEx.getInstance().copyProperties(dest, orig);
	}
}
