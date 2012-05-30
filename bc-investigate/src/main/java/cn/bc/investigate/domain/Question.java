package cn.bc.investigate.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import cn.bc.core.EntityImpl;

/**
 * 问题
 * 
 * @author dragon
 */
@Entity
@Table(name = "BC_IVG_QUESTION")
public class Question extends EntityImpl {
	private static final long serialVersionUID = 1L;
	
	/** 问题类型：单选 */
	public static final int TYPE_RADIO = 0;
	/** 问题类型：多选 */
	public static final int TYPE_CHECKBOX = 1;
	/** 问题类型：问答 */
	public static final int TYPE_TEXTAREA = 2;

	private String subject; // 标题
	private int orderNo; // 排序号
	private int type; // 类型:0-单选题,1-多选题,2-问答题,参考 TYPE_XXX 常数的定义
	private Questionary questionary; // 所属调查表
	private Set<QuestionItem> items;// 问题项:单选题多选题的每个选项对应一个,问答题对应一个
	private String option; // 问题项的布局配置，使用json格式，如控制选项水平、垂直、多行布局，控制问答题输入框的默认大小等

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "TYPE_")
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Column(name = "ORDER_")
	public int getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "PID", referencedColumnName = "ID")
	public Questionary getQuestionary() {
		return questionary;
	}

	public void setQuestionary(Questionary questionary) {
		this.questionary = questionary;
	}

	@OneToMany(mappedBy = "question", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy(value = "orderNo asc")
	public Set<QuestionItem> getItems() {
		return items;
	}

	public void setItems(Set<QuestionItem> items) {
		this.items = items;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
}
