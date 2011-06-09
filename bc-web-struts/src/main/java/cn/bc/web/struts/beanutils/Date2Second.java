package cn.bc.web.struts.beanutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * yyyy-MM-dd HH:mm:ss格式的日期
 * 
 * @author dragon
 * @since 2010-12-01
 * @see Date
 */
public class Date2Second extends java.util.Date {
	private static final long serialVersionUID = 4357927732529538059L;
	public static final DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Date2Second() {
		super();
	}

	public Date2Second(long date) {
		super(date);
	}

	public String toString() {
		return formater.format(this);
	}
}
