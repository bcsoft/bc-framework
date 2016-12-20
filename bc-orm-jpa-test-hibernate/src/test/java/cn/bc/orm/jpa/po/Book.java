package cn.bc.orm.jpa.po;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 书本
 *
 * @author dragon 2015-07-29
 */
@Entity
@Table(name = "t_jpa_book")
public class Book extends IdEntity<Long> implements Serializable {
    private String name;// 书名
    private Boolean bool;
    private Calendar date;
    private Date time;// time不能使用Calendar类型
    private Calendar timestamp;

    public Book() {
    }

    public Book(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getBool() {
        return bool;
    }

    public void setBool(Boolean bool) {
        this.bool = bool;
    }

    @Temporal(TemporalType.DATE)
    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    @Temporal(TemporalType.TIME)
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "{id: " + getId() + ", name: " + getName() + "}";
    }
}