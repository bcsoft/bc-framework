package cn.bc.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.core.exception.CoreException;

/**
 * 测试方法
 * 
 * @author dragon
 * @since 2011-08-30
 */
public class SchedulerTestMock {
	private static final Log logger = LogFactory
			.getLog(SchedulerTestMock.class);

	private int t1 = 0;

	public void success() {
		logger.fatal("run success method:" + (t1++));
	}

	private int t2 = 0;

	public void error() {
		logger.fatal("run error method:" + (t2++));
		if (t2 >= 2)
			throw new CoreException("test error in t2>=2");
	}
}
