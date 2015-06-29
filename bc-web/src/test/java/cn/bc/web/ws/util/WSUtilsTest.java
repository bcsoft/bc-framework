/**
 * 
 */
package cn.bc.web.ws.util;

import org.junit.Assert;
import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.converter.DataSetConverter;
import cn.bc.web.ws.dotnet.formater.WSCalendarFormatter;

/**
 * @author dragon
 * 
 */
public class WSUtilsTest {
	@Test
	public void testSendAndReceive1() {
		String soapUrl = "http://61.144.39.126/middle/WSMiddle.asmx";
		String soapAction = "http://tempuri.org/GetMasterWZ";
		String msgTpl = "<GetMasterWZ xmlns=\"http://tempuri.org/\">"
				+ "<strMasterID>{0}</strMasterID>\r\n"
				+ "<dWeiZhangKSRQ>{1}</dWeiZhangKSRQ>"
				+ "<dWeiZhangJZRQ>{2}</dWeiZhangJZRQ>" + "<strMsg></strMsg>"
				+ "</GetMasterWZ>";
		Object[] msgArgs = new Object[] {
				"17E0FFF7-7816-46A5-83A7-23D5C9F762AB", "2011-08-01",
				"2011-08-31" };

		String xml = WSUtils.sendAndReceive(soapUrl, soapAction, msgTpl,
				msgArgs);
		Assert.assertNotNull(xml);
		System.out.println(xml);
	}

	@Test
	public void testSendAndReceive2() {
		String soapUrl = "http://61.144.39.126/middle/WSMiddle.asmx";
		String soapAction = "http://tempuri.org/GetMasterWZ";
		String msgTpl = "<GetMasterWZ xmlns=\"http://tempuri.org/\">"
				+ "<strMasterID>{0}</strMasterID>\r\n"
				+ "<dWeiZhangKSRQ>{1}</dWeiZhangKSRQ>"
				+ "<dWeiZhangJZRQ>{2}</dWeiZhangJZRQ>" + "<strMsg></strMsg>"
				+ "</GetMasterWZ>";
		Object[] msgArgs = new Object[] {
				"17E0FFF7-7816-46A5-83A7-23D5C9F762AB", "2011-08-01",
				"2011-08-31" };

		DataSetConverter converter = new DataSetConverter();
		converter.addFormater("dateTime", new WSCalendarFormatter());
		DataSet dataSet = WSUtils.sendAndReceive(soapUrl, soapAction, msgTpl,
				msgArgs, converter);
		Assert.assertNotNull(dataSet);
		Assert.assertNotNull(dataSet.getColumns());
		System.out.println(dataSet);
	}
}
