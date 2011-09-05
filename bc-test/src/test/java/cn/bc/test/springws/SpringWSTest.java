package cn.bc.test.springws;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMText;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.junit.Test;

public class SpringWSTest {
	//@Test
	public void test01() throws Exception {
		// .net webService 地址
		String url = "http://61.144.39.126/middle/WSMiddle.asmx";
		// .net webService 命名空间
		String namespace = "http://tempuri.org/";
		// .net webService 需调用的方法
		String methodName = "GetDriverTaxiQY";
		// .net webService 的SOAPAction
		String soapActionURI = "http://tempuri.org/GetDriverTaxiQY";

		// 参数
		String strQYID = "17E0FFF7-7816-46A5-83A7-23D5C9F762AB";// 企业ID
		String strDriverNO = null;
		String strCarNO = null;
		String strMsg = "";
		Object[] opArgs = new Object[] { strQYID, strDriverNO, strCarNO, strMsg };


		
		
//		for (Object o : response) {
//			System.out.println(o.getClass());
//			if (o instanceof OMText) {
//				System.out.println(((OMText)o).getText());
//			} else {
//				System.out.println(o);
//			}
//			System.out.println("===========================================");
//		}
	}
}
