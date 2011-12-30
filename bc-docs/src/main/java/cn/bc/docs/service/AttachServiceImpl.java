package cn.bc.docs.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;

import cn.bc.BCConstants;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.docs.domain.Attach;

/**
 * 附件service接口的实现
 * 
 * @author dragon
 * 
 */
public class AttachServiceImpl extends DefaultCrudService<Attach> implements
		AttachService {
	private static Log logger = LogFactory.getLog(AttachServiceImpl.class);

	public List<Attach> findByPtype(String ptype, String puid) {
		return this
				.createQuery()
				.condition(
						new AndCondition()
								.add(new EqualsCondition("ptype", ptype))
								.add(new EqualsCondition("puid", puid))
								.add(new EqualsCondition("status",
										BCConstants.STATUS_ENABLED))
								.add(new OrderCondition("fileDate",
										Direction.Desc))).list();
	}

	public Attach loadByPtype(String ptype, String puid) {
		List<Attach> list = this
				.createQuery()
				.condition(
						new AndCondition()
								.add(new EqualsCondition("ptype", ptype))
								.add(new EqualsCondition("puid", puid))
								.add(new EqualsCondition("status",
										BCConstants.STATUS_ENABLED))
								.add(new OrderCondition("fileDate",
										Direction.Desc))).list(1, 1);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<Attach> doCopy(String fromPtype, String fromPuid,
			String toPtype, String toPuid, boolean keepAuthorInfo) {
		// TODO 设置附件的真正路径
		String dataPath = "/bcdata";

		Calendar now = Calendar.getInstance();
		if (logger.isDebugEnabled()) {
			logger.debug("复制附件:");
			logger.debug("dataPath=" + dataPath);
			logger.debug("fromPtype=" + fromPtype);
			logger.debug("fromPuid=" + fromPuid);
			logger.debug("toPtype=" + toPtype);
			logger.debug("toPuid=" + toPuid);
			logger.debug("keepAuthorInfo=" + keepAuthorInfo);
		}

		// 查找要复制的附件
		AndCondition c = new AndCondition();
		c.add(new EqualsCondition("puid", fromPuid))
				.add(new EqualsCondition("status", BCConstants.STATUS_ENABLED))
				.add(new OrderCondition("fileDate", Direction.Desc));
		if (fromPtype != null && fromPtype.length() > 0)
			c.add(new EqualsCondition("ptype", fromPtype));
		List<Attach> olds = this.createQuery().condition(c).list();
		if (null == olds || olds.isEmpty())
			return null;

		// 循环每一个附件进行拷贝
		List<Attach> news = new ArrayList<Attach>();
		Attach _new;
		for (Attach old : olds) {
			// _new = old.copy(dataPath, toPtype, toPuid);// 复制物理附件
			_new = new Attach();
			BeanUtils.copyProperties(this, _new);
			_new.setId(null);
			_new.setPtype(toPtype);
			_new.setPuid(toPuid);
			if (!keepAuthorInfo) {
				_new.setFileDate(now);
				// TODO 设置文件的作者信息
				_new.setModifiedDate(null);
				_new.setModifier(null);
			}
			news.add(_new);

			// ==拷贝具体的附件
			String sourcePath = dataPath + "/" + old.getPath();
			// 新文件存储的相对路径（年月），避免超出目录内文件数的限制
			String subFolder = new SimpleDateFormat("yyyyMM").format(now
					.getTime());
			// 要保存的新物理文件
			String realFileDir;// 所保存文件所在的目录的绝对路径名
			String relativeFilePath;// 所保存文件的相对路径名
			String realFilePath;// 所保存文件的绝对路径名
			String newFileName = toPtype
					+ (toPtype.length() > 0 ? "_" : "")
					+ new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(now
							.getTime()) + "." + old.getExtension();// 不含路径的文件名
			relativeFilePath = subFolder + "/" + newFileName;
			realFileDir = dataPath + "/" + subFolder;
			realFilePath = realFileDir + "/" + relativeFilePath;
			_new.setPath(relativeFilePath);

			// 构建新文件要保存到的目录
			File _fileDir = new File(realFileDir);
			if (!_fileDir.exists()) {
				if (logger.isDebugEnabled()) {
					logger.debug("mkdir=" + realFileDir);
				}
				_fileDir.mkdirs();
			}

			// TODO 保存一个附件记录

			// 检测源文件是否存在
			File source = new File(sourcePath);
			if (!source.exists()) {
				logger.error("源文件已不存在，忽略复制：sourcePath=" + sourcePath);
				break;
			}

			// 复制源文件
			try {
				FileUtils.copyFile(source, new File(realFilePath));
			} catch (IOException e) {
				logger.error("复制源文件失败，忽略不作处理：srcFile=" + sourcePath
						+ ",destFile=" + realFilePath);
			}
		}

		// 保存新复制的附件记录
		this.save(news);

		// 返回复制的新附件记录
		return news;
	}
}
