/**
 * 
 */
package cn.bc.core.export;

import java.io.OutputStream;

import cn.bc.core.exception.CoreException;

/**
 * 通用的数据导出接口
 * @author dragon
 *
 */
public interface Exporter {
	/**导出到流
	 * @param outputStream
	 * @throws CoreException
	 */
	void exportTo(OutputStream outputStream) throws Exception;
	
	/**导出到文件
	 * @param outputFile 文件全路径名
	 * @throws CoreException
	 */
	void exportTo(String outputFile) throws Exception;
}
