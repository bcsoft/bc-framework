/**
 * 
 */
package cn.bc.log.event;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.BCConstants;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;
import cn.bc.web.struts2.event.ExportViewDataEvent;

/**
 * 用户导出视图数据的监听器：记录操作日志
 * 
 * @author dragon
 * 
 */
public class ExportViewDataNotifier4OperateLog implements
		ApplicationListener<ExportViewDataEvent> {
	private static Log logger = LogFactory
			.getLog(ExportViewDataNotifier4OperateLog.class);
	private OperateLogService operateLogService;
	private AttachService attachService;

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public void onApplicationEvent(ExportViewDataEvent event) {
		// 操作日志
		OperateLog log = operateLogService.saveWorkLog(event.getPtype(),
				event.getPid(), event.getSubject(), null,
				OperateLog.OPERATE_EXPORT);

		// 构建下载数据对应的附件记录
		Attach attach = new Attach();
		attach.setAppPath(false);
		attach.setAuthor(log.getAuthor());
		attach.setFileDate(log.getFileDate());
		attach.setFormat(event.getFileType());
		attach.setPtype(OperateLog.class.getSimpleName());
		attach.setPuid(log.getUid());
		attach.setSize(event.getData().length);
		attach.setStatus(BCConstants.STATUS_ENABLED);
		attach.setSubject(event.getSubject());

		// 构建文件存储的路径
		String path = "export/";
		path += log.getAuthor().getCode();// 帐号名作为子目录;
		Calendar now = Calendar.getInstance();
		path += "/"
				+ new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(now
						.getTime()) + "[" + event.getPtype() + "]" + "."
				+ event.getFileType();
		File file = new File(Attach.DATA_REAL_PATH + "/" + path);
		if (!file.getParentFile().exists()) {
			if (logger.isWarnEnabled()) {
				logger.warn("mkdir=" + file.getParentFile().getAbsolutePath());
			}
			file.getParentFile().mkdirs();
		}
		attach.setPath(path);
		if (logger.isDebugEnabled()) {
			logger.debug("path=" + path);
		}

		// 保存物理文件
		try {
			FileUtils.writeByteArrayToFile(file, event.getData());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

		// 保存附件记录
		attachService.save(attach);
	}
}
