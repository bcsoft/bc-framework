package cn.bc.orm.jpa;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * ID基类
 */
@MappedSuperclass
public abstract class IdEntity<ID> {
    private ID id;

    /**
     * 1. Overriding the Default JPA Sequence: http://www.georgestragand.com/jpaseq.html
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}