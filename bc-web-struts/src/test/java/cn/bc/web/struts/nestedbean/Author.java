package cn.bc.web.struts.nestedbean;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;

public class Author {
	private long id;
	private String name;
	private String name2;
	private Date date;
	private Calendar calendar;
	private Address address;
	
	//Mapped Properties：
	private Map<String,Object> mapValues = new HashMap<String,Object>();
	
	private DynaBean dynaBean;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName2() {
		return name2;
	}
	public void setName2(String name2) {
		this.name2 = name2;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}

	public void setMapValue(String key, Object value) {
		mapValues.put(key, value);
    }
    public Object getMapValue(String key) {
        return mapValues.get(key);
    }
	
	public DynaBean getDynaBean() {
		return dynaBean;
	}
	public void setDynaBean(DynaBean dynaBean) {
		this.dynaBean = dynaBean;
	}
}
