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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.db.jdbc.RowMapper;

/**
 * JDBC的辅助函数库
 * 
 * @author dragon
 * @since 1.0.0
 */
public class JdbcUtils {
	static Log logger = LogFactory.getLog(JdbcUtils.class);

	/** 数据库类型：mysql */
	public static final String DB_MYSQL = "mysql";
	/** 数据库类型：oracle */
	public static final String DB_ORACLE = "oracle";
	/** 数据库类型：mssql */
	public static final String DB_MSSQL = "mssql";

	/** 系统数据库类型 */
	public static String dbtype;

	private JdbcUtils() {
	}

	public void setDbtype(String dbtype) {
		JdbcUtils.dbtype = dbtype;
		logger.fatal("dbtype=" + dbtype);
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