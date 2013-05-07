package cn.bc.business.spider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;

import cn.bc.spider.parser.Parser;
import cn.bc.template.util.FreeMarkerUtils;

/**
 * 网上车管所未登录状态获取交通违法信息的数据解析器
 * 
 * @author dragon
 * 
 */
public class WSCGSUnloginParser implements Parser<String> {
	private static Log logger = LogFactory.getLog(WSCGSUnloginParser.class);

	public String parse(Object data) {
		// 将数据格式化为html table
		// Collection<Map<String, Object>> jsons = (Collection<Map<String,
		// Object>>) data;
		InputStream is;
		try {
			// is = new ClassPathResource(
			// "cn/bc/business/spider/tpl/wscgu-unlogin.ftl")
			// .getInputStream();
			is = new FileInputStream("d:/wscgu-unlogin.ftl");
			String tpl = FileCopyUtils.copyToString(new InputStreamReader(is));
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("data", data);
			if (logger.isDebugEnabled()) {
				logger.debug("tpl0=" + tpl);
				logger.debug("data=" + data.getClass());
				logger.debug("args=" + args);
			}
			tpl = FreeMarkerUtils.format(tpl, args);
			if (logger.isDebugEnabled()) {
				logger.debug("tpl1=" + tpl);
			}
			return tpl;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
