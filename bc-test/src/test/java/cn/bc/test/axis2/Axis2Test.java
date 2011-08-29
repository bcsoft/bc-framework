package cn.bc.test.axis2;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMText;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.junit.Test;

public class Axis2Test {
	@Test
	public void test01() throws Exception {
		// .net webService 地址
		String url = "http://61.144.39.126/middle/WSMiddle.asmx";
		// .net webService 命名空间
		String namespace = "http://tempuri.org/";
		// .net webService 需调用的方法
		String methodName = "GetDriverTaxiQY";
		// .net webService 的SOAPAction
		String soapActionURI = "http://tempuri.org/GetDriverTaxiQY";

		RPCServiceClient serviceClient = new RPCServiceClient();
		Options options = serviceClient.getOptions();
		EndpointReference targetEPR = new EndpointReference(url);
		options.setTo(targetEPR);
		options.setAction(soapActionURI);

		// 参数
		String strQYID = "17E0FFF7-7816-46A5-83A7-23D5C9F762AB";// 企业ID
		String strDriverNO = null;
		String strCarNO = null;
		String strMsg = "";
		Object[] opArgs = new Object[] { strQYID, strDriverNO, strCarNO, strMsg };

		// 设定操作的名称
		QName opQName = new QName(namespace, methodName);
		// QName opQName = new QName(namespace, methodName);
		// 设定返回值

		Class<?>[] opReturnType = new Class[] { Object.class };

		// 操作需要传入的参数已经在参数中给定，这里直接传入方法中调用
		Object[] response = serviceClient.invokeBlocking(opQName, opArgs,
				opReturnType);
		for (Object o : response) {
			System.out.println(o.getClass());
			if (o instanceof OMText) {
				System.out.println(((OMText)o).getText());
			} else {
				System.out.println(o);
			}
			System.out.println("===========================================");
		}
	}
}
