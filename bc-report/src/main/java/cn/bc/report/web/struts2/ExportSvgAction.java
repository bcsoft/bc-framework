/**
 * 
 */
package cn.bc.report.web.struts2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.core.exception.CoreException;
import cn.bc.web.util.WebUtils;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 导出svg为文件的处理action
 * 
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ExportSvgAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	// private static Log logger = LogFactory.getLog(ExportSvgAction.class);
	// 下载的文件名（不包含扩展名部分）
	private String filename;
	// 下载类型，值为image/png、image/jpeg、application/pdf、image/svg xml
	private String type;
	// 导出的宽度
	private int width;
	// svg的html内容，格式为"<svg ...>...</svg>"
	private String svg;
	private InputStream inputStream;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getSvg() {
		return svg;
	}

	public void setSvg(String svg) {
		this.svg = svg;
	}

	// 从下载文件原始存放路径读取得到文件输出流
	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) throws Exception {
		this.inputStream = inputStream;
	}

	public String execute() throws Exception {
		// 转换后的临时文件，用batik生成，下载的文件就是这个
		String outfile;

		List<String> args = new ArrayList<String>();

		// 处理转换类型
		String ext = null;
		if ("image/png".equalsIgnoreCase(type)) {
			args.add("-m");
			args.add("image/png");
			ext = "png";
		} else if ("image/jpeg".equalsIgnoreCase(type)) {
			args.add("-m");
			args.add("image/jpeg");
			ext = "jpg";

		} else if ("application/pdf".equalsIgnoreCase(type)) {
			args.add("-m");
			args.add("application/pdf");
			ext = "pdf";

		} else if ("image/svg+xml".equalsIgnoreCase(type)) {
			ext = "svg";
		}

		// 根据type重新设置filename,使其包含文件的扩展名
		this.filename = filename + "." + ext;

		// 指定转换的编码,如果不指定，实测在maven的win7命令行下mvn jetty:run,下载图表时后台报错：
		//
		String encode = "UTF-8";
		if ("svg".equals(ext)) {// svg格式无需转换
			this.inputStream = new ByteArrayInputStream(this.getSvg().getBytes(
					encode));
		} else if (ext != null) {// 使用batik执行转换
			String tempPath = WebUtils.rootPath + File.separator + "temp"
					+ File.separator + new Date().getTime();

			// 将svg文件保存到临时文件夹
			File outF = new File(tempPath + ".svg");
			FileOutputStream fops = new FileOutputStream(outF);
			if(this.svg.indexOf("height=\"-1\"") != -1){
				//处理highchart导出饼图的错误
				this.svg = this.svg.replace("height=\"-1\"", "height=\"0\"");
			}
			fops.write(this.getSvg().getBytes(encode));
			fops.close();

			// 添加宽度参数
			if (this.width > 0) {
				args.add("-w");
				args.add(String.valueOf(this.width));
			}

			// 添加输出文件参数
			outfile = tempPath + "." + ext;
			args.add("-d");
			args.add(outfile);

			// 添加关闭安全检测的参数
			args.add("-scriptSecurityOff");

			// 添加要转换的文件
			args.add(tempPath + ".svg");

			// 调用batik执行转换(将会生成转换好的文件outfile)
			new org.apache.batik.apps.rasterizer.Main(
					args.toArray(new String[0])).execute();

			// 返回转换后的文件流
			this.inputStream = new FileInputStream(new File(outfile));
			// this.inputStream = ServletActionContext.getServletContext()
			// .getResourceAsStream(outfile);
		} else {
			// 错误的类型
			throw new CoreException("Invalid type");
		}

		return SUCCESS;
	}
}
