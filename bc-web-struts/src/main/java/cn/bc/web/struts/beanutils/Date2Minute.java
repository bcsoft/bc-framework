package cn.bc.web.struts.beanutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * yyyy-MM-dd HH:mm格式的日期
 * 
 * @author dragon
 * @since 2010-12-01
 * @see Date
 */
public class Date2Minute extends java.util.Date {
	private static final long serialVersionUID = 8589342989575045398L;
	public static final DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public Date2Minute() {
		super();
	}

	public Date2Minute(long date) {
		super(date);
	}

	public String toString() {
		return formater.format(this);
	}
}
