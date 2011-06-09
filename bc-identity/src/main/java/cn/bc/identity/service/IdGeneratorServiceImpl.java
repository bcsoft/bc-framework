package cn.bc.identity.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.core.dao.CrudDao;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.domain.IdGenerator;
import cn.bc.identity.service.IdGeneratorService;

/**
 * 标识生成器Service接口的实现
 * @author dragon
 *
 */
public class IdGeneratorServiceImpl implements IdGeneratorService {
	private static Log logger = LogFactory.getLog(IdGeneratorServiceImpl.class);
	protected CrudDao<IdGenerator> idGeneratorDao;

	@Autowired
	public void setIdGeneratorDao(CrudDao<IdGenerator> idGeneratorDao) {
		this.idGeneratorDao = idGeneratorDao;
	}

	public Long nextValue(String type) {
		IdGenerator entity = idGeneratorDao.load(type);
		if (entity == null)
			throw new CoreException("type is not exist. type=" + type);
		entity.setValue(entity.getValue() + 1);
		idGeneratorDao.save(entity);
		return entity.getValue();
	}

	public String next(String type) {
		IdGenerator entity = idGeneratorDao.load(type);
		if (entity == null)
			throw new CoreException("type is not exist. type=" + type);
		entity.setValue(entity.getValue() + 1);
		idGeneratorDao.save(entity);
		return this.formatValue(type, entity.getValue(), entity.getFormat());
	}

	public Long currentValue(String type) {
		IdGenerator entity = idGeneratorDao.load(type);
		if (entity == null)
			throw new CoreException("type is not exist. type=" + type);
		return entity.getValue();
	}

	public String current(String type) {
		IdGenerator entity = idGeneratorDao.load(type);
		if (entity == null)
			throw new CoreException("type is not exist. type=" + type);
		return this.formatValue(type, entity.getValue(), entity.getFormat());
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

	public void save(IdGenerator generator) {
		idGeneratorDao.save(generator);
	}
}
