/**
 * 
 */
package cn.bc.test.axis2;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 通过企业ID得到企业违章纪录
 * 
 * @author dragon
 * 
 */
public class GetMasterWZTest {
	private String qiYeID;// 企业ID
	private String soapUrl;// .net webService 地址
	private String soapNamespace;// .net webService 命名空间
	private String soapActionURIPrefix;// .net webService 的SOAPAction的前缀

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		qiYeID = "17E0FFF7-7816-46A5-83A7-23D5C9F762AB";
		soapUrl = "http://61.144.39.126/middle/WSMiddle.asmx";
		soapNamespace = "http://tempuri.org/";
		soapActionURIPrefix = "http://tempuri.org/";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {

	}

	@Test
	// 通过企业ID得到企业违章纪录
	public void testGetMasterWZ() throws Exception {
		String methodName = "GetMasterWZ";
		// .net webService 的SOAPAction
		String soapActionURI = soapActionURIPrefix + methodName;

		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(soapUrl);
		options.setTo(targetEPR);
		options.setAction(soapActionURI);

		// 参数
		String fromDate = "2011-08-01 00:00:00";
		String toDate = "2011-09-01 00:00:00";
		String errorMsg = "";
		Object[] args = new Object[] { qiYeID, fromDate, toDate, errorMsg };

		// 设定操作的名称
		QName qname = new QName(soapNamespace, methodName);

		// 设定返回值类型
		Class<?>[] returnType = new Class[] { Object.class };

		// 操作需要传入的参数已经在参数中给定，这里直接传入方法中调用
		Object[] response = serviceClient.invokeBlocking(qname, args,
				returnType);

		// 输出测试信息
		debugInfo(response);
	}

	@Test
	// 公交违法记录信息查询接口
	public void testSearchPublicTransport() throws Exception {
		String methodName = "SearchPublicTransport";
		// .net webService 的SOAPAction
		String soapActionURI = soapActionURIPrefix + methodName;

		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(soapUrl);
		options.setTo(targetEPR);
		options.setAction(soapActionURI);

		// 参数
		String fromDate = "2011-01-01 00:00:00";
		String toDate = "2011-09-01 00:00:00";
		String errorMsg = "";
		Object[] args = new Object[] { qiYeID, fromDate, toDate, errorMsg };

		// 设定操作的名称
		QName qname = new QName(soapNamespace, methodName);

		// 设定返回值类型
		Class<?>[] returnType = new Class[] { Object.class };

		// 操作需要传入的参数已经在参数中给定，这里直接传入方法中调用
		Object[] response = serviceClient.invokeBlocking(qname, args,
				returnType);

		// 输出测试信息
		debugInfo(response);
	}

	@Test
	// 根据企业ID获取该企业的投诉和建议列表
	public void testGetAccuseByQYID() throws Exception {
		String methodName = "GetAccuseByQYID";
		// .net webService 的SOAPAction
		String soapActionURI = soapActionURIPrefix + methodName;

		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(soapUrl);
		options.setTo(targetEPR);
		options.setAction(soapActionURI);

		// 参数
		String fromDate = "2011-01-01 01:00:00";
		String toDate = "2011-09-01 23:59:59";
		String errorMsg = "";
		Object[] args = new Object[] { qiYeID, fromDate, toDate, errorMsg };

		// 设定操作的名称
		QName qname = new QName(soapNamespace, methodName);

		// 设定返回值类型
		Class<?>[] returnType = new Class[] { Object.class };

		// 操作需要传入的参数已经在参数中给定，这里直接传入方法中调用
		Object[] response = serviceClient.invokeBlocking(qname, args,
				returnType);

		// 输出测试信息
		debugInfo(response);
	}

	private void debugInfo(Object[] response) {
		for (Object o : response) {
			if (o != null) {
				System.out.println(o.getClass());
				if (o instanceof OMText) {
					System.out.println(((OMText) o).getText());
				} else if (o instanceof OMElement) {
					System.out.println(((OMElement) o).getText());
				} else {
					System.out.println("==class==:" + o);
				}
			} else {
				System.out.println("null");
			}
			System.out.println("===========================================");
		}
	}
}
