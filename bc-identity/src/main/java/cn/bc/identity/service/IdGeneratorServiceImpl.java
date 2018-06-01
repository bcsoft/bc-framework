package cn.bc.identity.service;

import cn.bc.BCConstants;
import cn.bc.identity.dao.IdGeneratorDao;
import cn.bc.identity.domain.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 标识生成器Service接口的实现
 *
 * @author dragon
 */
public class IdGeneratorServiceImpl implements IdGeneratorService {
  private static Logger logger = LoggerFactory.getLogger(IdGeneratorServiceImpl.class);
  protected IdGeneratorDao idGeneratorDao;

  @Autowired
  public void setIdGeneratorDao(IdGeneratorDao idGeneratorDao) {
    this.idGeneratorDao = idGeneratorDao;
  }

  public Long nextValue(String type) {
    Long value = idGeneratorDao.currentValue(type);
    if (value == null) {
      // 没有的自动创建一个
      value = new Long(1);
      idGeneratorDao.save(type, value, "${T}.${V}");
      return value;
    }

    // 累加1
    value++;

    // 保存新的值
    idGeneratorDao.updateValue(type, value);

    return value;
  }

  public String next(String type) {
    Long value;
    IdGenerator entity = idGeneratorDao.load(type);
    if (entity == null) {
      // 没有的自动创建一个
      value = new Long(1);
      idGeneratorDao.save(type, value, "${T}.${V}");
      return this.formatValue(type, value, "${T}.${V}");
    } else {
      value = entity.getValue() + 1;
      idGeneratorDao.updateValue(type, value);
      return this.formatValue(type, value, entity.getFormat());
    }
  }

  public Long currentValue(String type) {
    Long value = idGeneratorDao.currentValue(type);
    if (value == null) {
      // 没有的自动创建一个
      value = new Long(0);
      idGeneratorDao.save(type, value, "${T}.${V}");
      return value;
    }
    return value;
  }

  public String current(String type) {
    Long value;
    IdGenerator entity = idGeneratorDao.load(type);
    if (entity == null) {
      // 没有的自动创建一个
      value = new Long(0);
      idGeneratorDao.save(type, value, "${T}.${V}");
      return this.formatValue(type, value, "${T}.${V}");
    } else {
      value = entity.getValue();
      return this.formatValue(type, value, entity.getFormat());
    }
  }

  // 格式化
  protected String formatValue(String type, Long value, String format) {
    if (logger.isDebugEnabled())
      logger.debug("formatValue:type=" + type + ";value=" + value
        + ";format=" + format);
    if (format == null || format.length() == 0) {
      return String.valueOf(value);
    } else {
      // TODO:${DATE},${TIME},${S}
      return format.replaceAll("\\$\\{T\\}", type).replaceAll(
        "\\$\\{V\\}", value.toString());
    }
  }

  private final static SimpleDateFormat format4month = new SimpleDateFormat(
    "yyyyMM");

  public String nextSN4Month(String type, String pattern) {
    // 获取当前月份信息yyyyMMdd
    String yyyyMM = format4month.format(new Date());

    // 格式化流水后
    Long num = this.nextValue(type + "." + yyyyMM);

    // 合并返回
    return yyyyMM + BCConstants.SN_SPLIT_SYMBOL + new DecimalFormat(pattern).format(num);
  }

  public String nextSN4Month(String type) {
    return nextSN4Month(type, "0000");
  }

  private final static SimpleDateFormat format4day = new SimpleDateFormat(
    "yyyyMMdd");

  public String nextSN4Day(String type) {
    return nextSN4Day(type, "0000");
  }

  public String nextSN4Day(String type, String pattern) {
    // 获取当前月份信息yyyyMMdd
    String yyyyMMdd = format4day.format(new Date());

    // 格式化流水后
    Long num = this.nextValue(type + "." + yyyyMMdd);

    // 合并返回
    return yyyyMMdd + BCConstants.SN_SPLIT_SYMBOL + new DecimalFormat(pattern).format(num);
  }
}
