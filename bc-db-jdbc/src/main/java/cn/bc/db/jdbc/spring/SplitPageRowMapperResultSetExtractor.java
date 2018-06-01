/**
 *
 */
package cn.bc.db.jdbc.spring;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dragon
 */
public class SplitPageRowMapperResultSetExtractor<T> implements
  ResultSetExtractor<List<T>> {
  private final int pageNo;// 页码，从1开始
  private final int pageSize;// 每页容量
  private final RowMapper<T> rowMapper;// 行包装器

  public SplitPageRowMapperResultSetExtractor(RowMapper<T> rowMapper,
                                              int pageNo, int pageSize) {
    Assert.notNull(rowMapper, "RowMapper is required");
    this.rowMapper = rowMapper;
    this.pageNo = (pageNo > 0 ? pageNo : 1);
    this.pageSize = (pageSize > 0 ? pageSize : 1);
  }

  public List<T> extractData(ResultSet rs) throws SQLException,
    DataAccessException {
    List<T> result = new ArrayList<T>();
    int rowNum = -1;
    int start = (pageNo - 1) * pageSize;
    int end = start + pageSize - 1;
    point:
    while (rs.next()) {
      ++rowNum;
      if (rowNum < start) {
        continue point;
      } else if (rowNum > end) {
        break point;
      } else {
        result.add(this.rowMapper.mapRow(rs, rowNum));
      }
    }
    return result;
  }
}
