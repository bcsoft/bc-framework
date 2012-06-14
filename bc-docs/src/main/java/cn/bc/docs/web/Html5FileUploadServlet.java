/**
 * 
 */
package cn.bc.docs.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.Context;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.domain.AttachHistory;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.util.WebUtils;

/**
 * Html5文件上传的实现.
 * <p>
 * 文件直接上传到Attach.DATA_REAL_PATH路径下，通过参数subdir(以/开头不以/结尾)可指定文件保存到的相对子路径
 * </p>
 * 
 * @author dragon
 * 
 */
public class Html5FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getLog(Html5FileUploadServlet.class);
	private static String extensions;// 上传类型限制，如 "jpg,jpeg,bmp,gif,png"，为空代表无限制
	private static long maxSize;// 上传文件大小限制，单位为字节，默认10M

	protected AttachService getAttachService() {
		return WebUtils.getBean(AttachService.class);
	}

	public void init() throws ServletException {
		// debug
		if (logger.isFatalEnabled()) {
			logger.fatal("dataRealDir=" + Attach.DATA_REAL_PATH);
		}
	}

	// 上传文件数据处理过程
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("subdir=" + request.getParameter("subdir"));
		}

		// 防止缓存的设置
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		if ("application/octet-stream".equals(request.getContentType())) {
			logger.debug("doPost4Html5");
			// HTML5上传
			doPost4Html5(request, response);
		} else {
			logger.debug("doPost4Html4");
			// HTML4普通文件上传
			// doPost4Html4(request, response);
		}
	}

	public void doPost4Html5(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String source = "";// 用户原始的本地文件路径信息
		String subdir = request.getParameter("subdir");// 子路径
		if (subdir == null)
			subdir = "";
		JSONObject json = new JSONObject();// 返回的信息

		try {
			// 获取当前用户信息
			SystemContext context = (SystemContext) request.getSession()
					.getAttribute(Context.KEY);
			if (context == null) {
				json.put("success", false);
				json.put("msg", "用户未登录或登录超时！");
				writeReturnJson(response, json);
				return;
			}

			// 获取上传文件名
			source = URLDecoder.decode(getHtml5FileName(request), "UTF-8");

			// 获取扩展名
			String extension = getExtension(source);

			// 检查文件类型
			if (!StringUtils.isEmpty(extensions)
					&& ("," + extensions.toLowerCase() + ",").indexOf(","
							+ extension.toLowerCase() + ",") == -1) {
				json.put("success", false);
				json.put("msg", "不允许上传此类型的文件");
				writeReturnJson(response, json);
				return;
			}

			// 获取上传文件流
			int i = request.getContentLength();
			logger.debug("contentLength=" + i);
			byte buffer[] = new byte[i];
			int j = 0;
			InputStream in = request.getInputStream();
			while (j < i) {
				int k = in.read(buffer, j, i - j);
				j += k;
			}

			// 检查文件是否为空
			int size = buffer.length;
			if (size == 0) {
				json.put("success", false);
				json.put("msg", "传文件不能为空");
				writeReturnJson(response, json);
				return;
			}

			// 检查文件大小是否超限
			if (maxSize > 0 && size > maxSize) {
				json.put("success", false);
				json.put("msg", "上传文件的大小超出限制");
				writeReturnJson(response, json);
				return;
			}

			// 文件存储的相对路径（年月），避免超出目录内文件数的限制
			Calendar now = Calendar.getInstance();
			String datedir = new SimpleDateFormat("yyyyMM").format(now
					.getTime());

			// 要保存的物理文件
			String realpath;// 绝对路径名
			String fileName = new SimpleDateFormat("yyyyMMddHHmmssSSSS")
					.format(now.getTime()) + "." + extension;// 不含路径的文件名
			realpath = Attach.DATA_REAL_PATH
					+ (subdir.length() > 0 ? "/" + subdir : "") + "/" + datedir
					+ "/" + fileName;

			// 构建文件要保存到的目录
			File file = new File(realpath);
			if (!file.getParentFile().exists()) {
				if (logger.isWarnEnabled()) {
					logger.warn("mkdir="
							+ file.getParentFile().getAbsolutePath());
				}
				file.getParentFile().mkdirs();
			}

			// 根目录下的子路径名
			String path = (subdir.length() > 0 ? subdir + "/" : "") + datedir
					+ "/" + fileName;

			// 创建文件上传日志
			String ptype = request.getParameter("ptype");
			String puid = request.getParameter("puid");
			if (ptype != null && puid != null) {
				AttachHistory history = new AttachHistory();
				history.setPtype(ptype);
				history.setPuid(puid);
				history.setType(AttachHistory.TYPE_UPLOAD);
				history.setAuthor(context.getUserHistory());
				history.setFileDate(now);
				history.setPath(path);
				history.setAppPath(false);
				history.setFormat(extension);
				history.setSubject(source);
				String[] c = WebUtils.getClient(request);
				history.setClientIp(c[0]);
				history.setClientInfo(c[2]);
				this.getAttachService().saveHistory(history);
			} else {
				logger.warn("没有指定ptype、puid参数，不保存文件上传记录");
			}

			// 保存到文件
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					realpath));
			out.write(buffer);
			out.close();

			// 返回成功信息
			json.put("success", true);
			json.put("msg", "上传成功");
			json.put("rootdir", Attach.DATA_REAL_PATH);// 根目录名
			json.put("subdir", subdir);// 子目录名
			json.put("to", datedir + "/" + fileName);// 子目录下的子路径名
			json.put("file", path);
			json.put("source", source);// 用户原始的本地文件路径信息
			writeReturnJson(response, json);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			try {
				json.put("success", false);
				json.put("msg", e.getMessage());
			} catch (JSONException e1) {
				logger.error(e.getMessage(), e);
			}
			writeReturnJson(response, json);
		}
	}

	// 获取使用html5上传的文件的名称
	// ref: Content-Disposition:attachment; name="filedata"; filename="xxxx.jpg"
	private static String getHtml5FileName(HttpServletRequest request) {
		String dispoString = request.getHeader("Content-Disposition");
		int iFindStart = dispoString.indexOf("name=\"") + 6;
		int iFindEnd = dispoString.indexOf("\"", iFindStart);
		iFindStart = dispoString.indexOf("filename=\"") + 10;
		iFindEnd = dispoString.indexOf("\"", iFindStart);
		String sFileName = dispoString.substring(iFindStart, iFindEnd);
		return sFileName;
	}

	// 获取文件的扩展名，如"png"
	private static String getExtension(String fileName) {
		int pos = fileName.lastIndexOf(".");
		return fileName.substring(pos + 1).toLowerCase();
	}

	// 使用I/O流输出 json格式的数据
	private static void writeReturnJson(HttpServletResponse response,
			JSONObject json) throws IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		out.flush();
		out.close();
	}
}