package cn.bc.db.jdbc.psql;

import cn.bc.core.util.SerializeUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import javax.json.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * postgres 字段类型为 bytea 的数据测试
 * Created by dragon on 2014/12/25.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
public class ByteaDataTest {
	private static Logger logger = LoggerFactory.getLogger(ByteaDataTest.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	public void getByteaData2Img() throws IOException {
		// activiti 部署流程产生的流程图
		String sql = "select bytes_ from act_ge_bytearray where deployment_id_ = '149500' and name_ = 'CarManEntry.CarManEntry.png'";
		logger.debug("sql={}", sql);
		byte[] bytes = this.jdbcTemplate.queryForObject(sql, byte[].class);
		logger.debug("bytes.length={}", bytes.length);

		// 保存为图片
		String path = "d:\\t\\CarManEntry.CarManEntry.png";
		FileCopyUtils.copy(bytes, new File(path));
		logger.debug("save to {}", path);
	}

	@Test
	public void getByteaData2List() throws IOException {
		// list 序列化保存的流程变量
		String sql = "select bytes_ from act_ge_bytearray where id_ = '3354068'";
		logger.debug("sql={}", sql);
		byte[] bytes = this.jdbcTemplate.queryForObject(sql, byte[].class);
		logger.debug("bytes.length={}", bytes.length);
		logger.debug("bytes={}", bytes);

		// 反序列化
		Object obj = SerializeUtils.deserialize(bytes);
		logger.debug("class={}", obj.getClass());
		logger.debug("obj={}", obj);
	}

	@Test
	public void getByteaDataFromJson() throws IOException {
		// list 序列化保存的流程变量
		String sql = "select bytes_::text from act_ge_bytearray where id_ = '3354068'";
		logger.debug("sql={}", sql);
		String bytes = this.jdbcTemplate.queryForObject(sql, String.class);
		logger.debug("bytes.length={}", bytes.length());
		logger.debug("bytes={}", bytes.getBytes());
	}

	@Test
	public void getJson() throws IOException {
		// list 序列化保存的流程变量
		String sql = "select wf__find_process_instance_detail('3349285')";
		logger.debug("sql={}", sql);
		String jsonString = this.jdbcTemplate.queryForObject(sql, String.class);
		logger.debug("jsonString.length={}", jsonString.length());
		logger.debug("jsonString={}", jsonString);

		JsonReader reader = Json.createReader(new StringReader(jsonString));
		JsonStructure j = reader.read();
		Assert.assertEquals(JsonValue.ValueType.OBJECT, j.getValueType());
		Assert.assertTrue(j instanceof JsonObject);
		Assert.assertEquals(jsonString, j.toString());
	}
}
