package cn.bc.template.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;

import org.junit.Test;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class MustacheTest {

	@Test
	public void test() throws IOException {
		HashMap<String, Object> scopes = new HashMap<String, Object>();
		scopes.put("name", "Mustache");
		scopes.put("feature", new Feature("Perfect!"));

		Writer writer = new OutputStreamWriter(System.out);
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache mustache = mf.compile(new StringReader(
				"{{name}}, {{feature.description}}!{{name.empty == true}}{{name.indexOf(\"s\")}}"), "example");
		mustache.execute(writer, scopes);
		writer.flush();
	}

	class Feature {
		String description;

		public Feature(String description) {
			this.description = description;
            //this.description.isEmpty();
		}
	}
}
