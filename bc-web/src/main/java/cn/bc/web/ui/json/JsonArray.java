package cn.bc.web.ui.json;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.web.ui.Render;

/**
 * jsons
 * 
 * @author dragon
 * 
 */
public class JsonArray implements Render {
	protected Log logger = LogFactory.getLog(getClass());
	public static final String APREFIX = "[";
	public static final String ASUFFIX = "]";

	protected List<Json> jsons = new ArrayList<Json>();

	public StringBuffer render(StringBuffer main) {
		main.append(APREFIX);
		int i = 0;
		for (Json e : jsons) {
			if (i > 0)
				main.append(Json.COMMA);
			main.append(e);
			i++;
		}
		main.append(ASUFFIX);
		if (logger.isDebugEnabled())
			logger.debug(main.toString());
		return main;
	}

	public void add(Json json) {
		jsons.add(json);
	}

	public String toString() {
		StringBuffer main = new StringBuffer();
		render(main);
		return main.toString();
	}

	public JsonArray clear() {
		jsons.clear();
		return this;
	}
}
