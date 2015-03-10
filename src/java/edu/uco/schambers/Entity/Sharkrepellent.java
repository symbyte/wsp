/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Large
 */
@Entity
@Table(name = "SHARKREPELLENT")
@DiscriminatorValue("S")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Sharkrepellent.findAll", query = "SELECT s FROM Sharkrepellent s"),
	@NamedQuery(name = "Sharkrepellent.findByProdid", query = "SELECT s FROM Sharkrepellent s WHERE s.prodid = :prodid"),
	@NamedQuery(name = "Sharkrepellent.findByVolume", query = "SELECT s FROM Sharkrepellent s WHERE s.volume = :volume"),
	@NamedQuery(name = "Sharkrepellent.findByEffectiveness", query = "SELECT s FROM Sharkrepellent s WHERE s.effectiveness = :effectiveness")})
public class Sharkrepellent extends Product implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "VOLUME")
	private Integer volume;
	@Size(max = 255)
        @Column(name = "EFFECTIVENESS")
	private String effectiveness;

	public Sharkrepellent() {
	}

	public Sharkrepellent(Integer prodid) {
		super(prodid);
	}


	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public String getEffectiveness() {
		return effectiveness;
	}

	public void setEffectiveness(String effectiveness) {
		this.effectiveness = effectiveness;
	}

	
}
