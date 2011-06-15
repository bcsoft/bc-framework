package cn.bc.test.axis1;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.Schema;
import org.junit.Test;

public class AxisTest {
	//@Test
	public void test01() throws Exception {
		// .net webService 地址
		String url = "http://61.144.39.126/middle/WSMiddle.asmx";
		// .net webService 命名空间
		String namespace = "http://tempuri.org/";
		// .net webService 需调用的方法
		String methodName = "GetDriverTaxiQY";
		String soapActionURI = "http://tempuri.org/GetDriverTaxiQY";
		Service service = new Service();

		Call call = (Call) service.createCall();

		call.setTargetEndpointAddress(new java.net.URL(url));
		call.setUseSOAPAction(true);
		// 这个地方没设对就会出现Server was unable to read request的错误
		call.setSOAPActionURI(soapActionURI);
		// 设置要调用的.net webService方法
		call.setOperationName(new QName(namespace, methodName));

		// 设置该方法的参数，strXXX为.net webService中的参数名称
		call.addParameter(new QName(namespace, "strQYID"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		call.addParameter(new QName(namespace, "strDriverNO"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		call.addParameter(new QName(namespace, "strCarNO"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.IN);
		call.addParameter(new QName(namespace, "strMsg"),
				org.apache.axis.encoding.XMLType.XSD_STRING,
				javax.xml.rpc.ParameterMode.INOUT);

		// 设置该方法的返回值
		call.setReturnType(XMLType.XSD_SCHEMA);
		
		String strQYID = "11";
		String strDriverNO = null;
		String strCarNO = null;
		String strMsg = "";
		// call.invoke(new Object[] { "kusix" }); 中"kusix"为传入参数值
		Object ret = (Object) call.invoke(new Object[] { strQYID, strDriverNO, strCarNO, strMsg });
		System.out.println("返回类型---> " + ret != null ? ret.getClass()
				.toString() : "null");
		System.out.println("返回结果---> " + ret);

		if (ret instanceof Schema) {
			org.apache.axis.types.Schema dataset = (Schema) ret;
			MessageElement[] mes = dataset.get_any();
			for (MessageElement me : mes) {
				System.out.println(me);
				System.out
						.println("===========================================");
			}
		}
	}
}
