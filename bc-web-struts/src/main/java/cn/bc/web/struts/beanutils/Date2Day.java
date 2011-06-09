package cn.bc.web.struts.beanutils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * yyyy-MM-dd格式的日期
 * 
 * @author dragon
 * @since 2010-12-01
 * @see Date
 */
public class Date2Day extends java.util.Date {
	private static final long serialVersionUID = 9128415361227816996L;
	public static final DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");

	public Date2Day() {
		super();
	}
	public Date2Day(long date) {
		super(date);
	}

	public String toString() {
		return formater.format(this);
	}
}
