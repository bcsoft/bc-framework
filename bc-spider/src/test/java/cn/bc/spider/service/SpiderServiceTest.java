package cn.bc.spider.service;

import org.junit.Assert;
import org.junit.Test;

public class SpiderServiceTest {
	private SpiderService spiderService;

	public SpiderServiceTest() {
		spiderService = new SpiderServiceImpl();
	}

	@Test
	public void testGetCaptcha() throws Exception {
		String url = "https://passport.baidu.com/cgi-bin/genimage?0013679162730156F5D6E41E0F7926A17D8F5AD729523395FB0C0838CB9DADC12B612FB5A773765553D3416201012AA9978BAE573C17E24EF4C5010984D0049B33E314946C1E624AFFA82FB4DFEE0831DC0F103023580F57962BB41FE099C1FE7EAFB3BFE512239007014224979AB21C66FD9D7F26E9645B0F98D40E9E43A332D516E277AC7657B6FCC92D431DC65E1D62F398B16AFECD46F8663A284596CBC111F518B069D553A1";
		String captcha = spiderService.getCaptcha(null, url);
		Assert.assertNotNull(captcha);
	}
}