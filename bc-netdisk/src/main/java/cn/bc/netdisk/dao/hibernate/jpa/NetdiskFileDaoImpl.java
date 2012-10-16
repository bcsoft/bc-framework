package cn.bc.netdisk.dao.hibernate.jpa;

import cn.bc.netdisk.dao.NetdiskFileDao;
import cn.bc.netdisk.domain.NetdiskFile;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 网络文件DAO接口的实现
 * 
 * @author zxr
 * 
 */
public class NetdiskFileDaoImpl extends HibernateCrudJpaDao<NetdiskFile> implements
		NetdiskFileDao {

}
