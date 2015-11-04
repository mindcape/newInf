package com.ipsg.inferneon.app.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Attribute jpa entity
 *
 */
@Entity
@Table(name = "ATTRIBUTE")
public class Attribute extends AbstractEntity{
	
	@Column(name = "ATT_NAME" , nullable = false, length = 30)
	private String attName;
    
    @Column(name = "ATT_TYPE" ,  nullable = true, length = 30)
	private String attType;
    
    @Column(name = "ATT_VALID_VALUES", nullable = true, length = 250)
	private String attValidValues;
	
	@ManyToOne
	
	private Project project;
	
    @Column(name = "ATT_ORDER")
	private int attOrder;

    public Attribute() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    public Attribute(String attributeName, String attributeType, String attributeValidValues,int attOrder) {
		super();
		this.attName = attributeName;
		this.attType = attributeType;
		this.attValidValues = attributeValidValues;		
		this.attOrder = attOrder;
	}

	public String getAttName() {
		return attName;
	}

	public void setAttName(String attName) {
		this.attName = attName;
	}

	public String getAttType() {
		return attType;
	}

	public void setAttType(String attType) {
		this.attType = attType;
	}

	public String getAttValidValues() {
		return attValidValues;
	}

	public void setAttValidValues(String attValidValues) {
		this.attValidValues = attValidValues;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getAttOrder() {
		return attOrder;
	}

	public void setAttOrder(int attOrder) {
		this.attOrder = attOrder;
	}




	

    
    
}
