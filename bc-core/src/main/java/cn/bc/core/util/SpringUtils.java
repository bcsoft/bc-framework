/*
 * Copyright 2010- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bc.core.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.MethodInvoker;

import cn.bc.core.exception.CoreException;

/**
 * Spring的辅助函数库
 * 
 * @author dragon
 * @since 1.0.0
 */
public class SpringUtils implements ApplicationContextAware {
	static Log logger = LogFactory.getLog(SpringUtils.class);
	private static ApplicationContext applicationContext;

	private SpringUtils() {
	}

	public void setApplicationContext(ApplicationContext _applicationContext)
			throws BeansException {
		applicationContext = _applicationContext;
	}

	/**
	 * 获取Bean对象
	 * 
	 * @param name
	 *            bean的配置名称
	 * @return bean对象
	 */
	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	/**
	 * 获取Bean对象
	 * 
	 * @param name
	 *            bean的配置名称
	 * @param requiredType
	 *            bean的类型
	 * @return bean对象
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	/**
	 * 获取Bean对象
	 * 
	 * @param requiredType
	 *            bean的类型
	 * @return bean对象
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 运行Bean中指定名称的方法并返回其返回值
	 * 
	 * @param object
	 *            object对象
	 * @param methodName
	 *            方法名
	 * @param arguments
	 *            参数值
	 * @return 运行bean中指定名称的方法的返回值
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 */
	public static Object invokeBeanMethod(String beanName, String methodName,
			Object[] arguments) {
		try {
			// 初始化
			MethodInvoker methodInvoker = new MethodInvoker();
			methodInvoker.setTargetObject(SpringUtils.getBean(beanName));
			methodInvoker.setTargetMethod(methodName);

			// 设置参数
			if (arguments != null && arguments.length > 0) {
				methodInvoker.setArguments(arguments);
			}

			// 准备方法
			methodInvoker.prepare();

			// 执行方法
			return methodInvoker.invoke();
		} catch (ClassNotFoundException e) {
			throw new CoreException(e);
		} catch (NoSuchMethodException e) {
			throw new CoreException(e);
		} catch (InvocationTargetException e) {
			throw new CoreException(e);
		} catch (IllegalAccessException e) {
			throw new CoreException(e);
		}
	}
}