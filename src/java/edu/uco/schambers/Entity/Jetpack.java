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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Large
 */
@Entity
@Table(name = "JETPACK")
@DiscriminatorValue("J")
@XmlRootElement
@NamedQueries(
{
	@NamedQuery(name = "Jetpack.findAll", query = "SELECT j FROM Jetpack j"),
	@NamedQuery(name = "Jetpack.findByProdid", query = "SELECT j FROM Jetpack j WHERE j.prodid = :prodid"),
	@NamedQuery(name = "Jetpack.findByFuel", query = "SELECT j FROM Jetpack j WHERE j.fuel = :fuel"),
	@NamedQuery(name = "Jetpack.findByEnginesize", query = "SELECT j FROM Jetpack j WHERE j.enginesize = :enginesize")
})
public class Jetpack extends Product implements Serializable
{

	private static final long serialVersionUID = 1L;
	@Size(max = 255)
	@Column(name = "FUEL")
	private String fuel;
	@Size(max = 255)
	@Column(name = "ENGINESIZE")
	private String enginesize;

	public Jetpack()
	{
	}

	public Jetpack(Integer prodid)
	{
		super(prodid);
	}

	public String getFuel()
	{
		return fuel;
	}

	public void setFuel(String fuel)
	{
		this.fuel = fuel;
	}

	public String getEnginesize()
	{
		return enginesize;
	}

	public void setEnginesize(String enginesize)
	{
		this.enginesize = enginesize;
	}

	public String getProductInfo()
	{
		return this.getProdname() + " Jetpack, " + this.enginesize + " " + this.fuel + " engine";
	}

}
