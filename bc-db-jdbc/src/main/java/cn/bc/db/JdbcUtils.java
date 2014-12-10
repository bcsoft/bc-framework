/*
 * Copyright 2010- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.bc.db;

import cn.bc.db.jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * JDBC的辅助函数库
 * 
 * @author dragon
 * @since 1.0.0
 */
public class JdbcUtils {
	static Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

	/** 数据库类型：mysql */
	public static final String DB_MYSQL = "mysql";
	/** 数据库类型：postgresql */
	public static final String DB_POSTGRESQL = "postgresql";
	/** 数据库类型：oracle */
	public static final String DB_ORACLE = "oracle";
	/** 数据库类型：mssql */
	public static final String DB_MSSQL = "mssql";

	/** 系统数据库类型 */
	public static String dbtype;

	private JdbcUtils() {
	}

	/**
	 * 获取生成序列值的方法字符串
	 * 
	 * @param sequenceName
	 * @return
	 */
	public static String getSequenceValue(String sequenceName) {
		if (JdbcUtils.dbtype.equalsIgnoreCase(DB_POSTGRESQL)) {
			return "nextval('" + sequenceName + "')";
		} else if (JdbcUtils.dbtype.equalsIgnoreCase(DB_ORACLE)) {
			return sequenceName + ".nextval";
		} else {
			throw new RuntimeException("this database unsupport sequence.");
		}
	}

	/**
	 * 获取数据库生成当前时间的函数字符串
	 * 
	 * @param sequenceName
	 * @return
	 */
	public static String getNowValue() {
		if (JdbcUtils.dbtype.equalsIgnoreCase(DB_ORACLE)) {
			return "sysdate";
		} else {
			return "now()";
		}
	}

	public void setDbtype(String dbtype) {
		JdbcUtils.dbtype = dbtype;
		logger.warn("dbtype={}", dbtype);
	}

	/**
	 * 对Object数据执行映射转换
	 * 
	 * @param rs
	 *            要转换的数据
	 * @param rowMapper
	 *            数据行的映射转换器
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> mapRows(List<Object[]> rs, RowMapper<T> rowMapper) {
		if (rs != null) {
			if (rowMapper != null) {
				List<T> mr = new ArrayList<T>();
				for (int j = 0; j < rs.size(); j++) {
					mr.add(rowMapper.mapRow(rs.get(j), j));
				}
				return mr;
			} else {
				return (List<T>) rs;
			}
		} else {
			return null;
		}
	}
}