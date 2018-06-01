package cn.bc.log.service;

import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.dao.SyslogDao;
import cn.bc.log.domain.Syslog;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 系统日志service接口的实现
 *
 * @author dragon
 */
public class SyslogServiceImpl extends DefaultCrudService<Syslog> implements
  SyslogService {
  //private SyslogDao syslogDao;

  @Autowired
  public void setSyslogDao(SyslogDao syslogDao) {
    //this.syslogDao = syslogDao;
    this.setCrudDao(syslogDao);
  }
}
