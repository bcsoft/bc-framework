package cn.bc.core.util;


import org.junit.Assert;
import org.junit.Test;

public class PinYinUtilsTest {
	// 生僻字
	private String[] ShengPiZis = new String[] { "妃", "冼", "棣", "嘹", "覃", "奕",
			"晟", "昶", "毓", "嵩", "炜", "琦", "煜", "煊", "骥", "煊", "淦", "祺", "泓",
			"铎", "峧", "纡", "薇", "蓓雯" };
	// 生僻字的拼音
	private String[] ShengPiZiPinYins = new String[] { "fei", "xian", "di",
			"liao", "tan", "yi", "sheng", "chang", "yu", "song", "wei", "qi",
			"yu", "xuan", "ji", "xuan", "gan", "qi", "hong", "duo", "jiao",
			"yu", "wei", "beiwen", };

	@Test
	public void testMain() {
		for (int i = 0; i < ShengPiZis.length; i++) {
			Assert.assertEquals(ShengPiZiPinYins[i],
					PinYinUtils.getLowercasePingYin(ShengPiZis[i]));
		}
		Assert.assertEquals("abchuangrongjixyz",
				PinYinUtils.getLowercasePingYin("abc黄荣基xyz"));
		Assert.assertEquals("abchuangrongji123",
				PinYinUtils.getLowercasePingYin("abc黄荣基123"));
		Assert.assertEquals("abchuangarong1ji123",
				PinYinUtils.getLowercasePingYin("abc黄a荣1基123"));

		Assert.assertEquals("abcHUANGRONGJIxyz",
				PinYinUtils.getUppercasePingYin("abc黄荣基xyz"));
		Assert.assertEquals("abcHUANGRONGJI123",
				PinYinUtils.getUppercasePingYin("abc黄荣基123"));
		Assert.assertEquals("abcHUANGaRONG1JI123",
				PinYinUtils.getUppercasePingYin("abc黄a荣1基123"));
	}

	//@Test
	public void testGetPinYin() {
		String[] zhs = new String[] { "总公司", "信息部", "销售部", "财务部",
				"行政部", "北京分公司", "上海分公司" };
		for (int i = 0; i < zhs.length; i++) {
			System.out.println(zhs[i] + " = "
					+ PinYinUtils.getLowercasePingYin(zhs[i]));
		}
	}
}
