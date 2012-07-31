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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
}