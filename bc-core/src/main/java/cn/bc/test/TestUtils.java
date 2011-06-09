/**
 * 
 */
package cn.bc.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 测试用
 * @author dragon
 *
 */
public class TestUtils {
	private static final Log logger = LogFactory.getLog(TestUtils.class);
	private static String dbSequence;
	
	public TestUtils(String dbSequence){
		TestUtils.dbSequence = dbSequence;
		logger.info("sequence=" + dbSequence);
	}
	public static String getDbSequence() {
		return dbSequence;
	}
//	public static void setDbSequence(String dbSequence) {
//		TestUtils.dbSequence = dbSequence;
//		logger.info("sequence=" + dbSequence);
//	}
}
