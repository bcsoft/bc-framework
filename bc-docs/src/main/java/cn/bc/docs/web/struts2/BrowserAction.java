/**
 *
 */
package cn.bc.docs.web.struts2;

import cn.bc.core.util.StringUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.AttachUtils;
import cn.bc.web.util.WebUtils;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 图片剪切处理Action
 *
 * @author dragon
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BrowserAction extends ActionSupport {
  //private static Log logger = LogFactory.getLog(BrowserAction.class);
  private static final long serialVersionUID = 1L;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  public List<Map<String, Object>> browsers;

  // 返回下载系统支持的浏览器列表的页面
  @Override
  public String execute() throws Exception {
    // 获取最新浏览器列表
    this.browsers = this.findLatest();
    for (Map<String, Object> m : this.browsers) {
      m.put("size_info", StringUtils.formatSize(Long.parseLong(m.get("size_").toString())));
    }
    return super.execute();
  }

  public int id;
  public String contentType;
  public long contentLength;
  public InputStream inputStream;
  public String filename;

  // 下载
  public String download() throws Exception {
    Map<String, Object> b = this.load(this.id);
    this.contentType = AttachUtils.getContentType((String) b.get("format"));
    String fn = b.get("name") + "-" + b.get("ver_");
    fn += "-" + b.get("os_type");

    int os_bit = (Integer) b.get("os_bit");
    if (os_bit == 1) {
      fn += "-32位";
    } else if (os_bit == 2) {
      fn += "-64位";
    }

    fn += "." + b.get("format");
    this.filename = WebUtils.encodeFileName(ServletActionContext.getRequest(), fn);

    File file = new File(Attach.DATA_REAL_PATH + "/" + b.get("path"));
    this.contentLength = file.length();
    this.inputStream = new FileInputStream(file);

    return SUCCESS;
  }

  private Map<String, Object> load(int id) {
    return jdbcTemplate.queryForMap("select * from bc_browser where id = " + id);
  }


  private List<Map<String, Object>> findLatest() {
    String sql = "select * from bc_browser b1\r\n"
      + "	where not exists (\r\n"
      + "		select 0 from bc_browser b2\r\n"
      + "			where b2.os_type = b1.os_type and b2.code = b1.code and b2.release_date > b1.release_date\r\n"
      + "	)\r\n"
      + "	and support = true"
      + "	order by release_date desc";
    return jdbcTemplate.queryForList(sql);
  }
}