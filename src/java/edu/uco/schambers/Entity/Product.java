/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.Entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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
@Table(name = "PRODUCT")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="prodtype")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
	@NamedQuery(name = "Product.findByProdid", query = "SELECT p FROM Product p WHERE p.prodid = :prodid"),
	@NamedQuery(name = "Product.findByProdname", query = "SELECT p FROM Product p WHERE p.prodname = :prodname"),
	@NamedQuery(name = "Product.findByProdprice", query = "SELECT p FROM Product p WHERE p.prodprice = :prodprice"),
	@NamedQuery(name = "Product.findByQuantity", query = "SELECT p FROM Product p WHERE p.quantity = :quantity"),
	@NamedQuery(name = "Product.findByProdtype", query = "SELECT p FROM Product p WHERE p.prodtype = :prodtype")})
public abstract class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
        @Basic(optional = false)
        @NotNull
        @Column(name = "PRODID")
	private Integer prodid;
	@Size(max = 255)
        @Column(name = "PRODNAME")
	private String prodname;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Column(name = "PRODPRICE")
	private Double prodprice;
	@Column(name = "QUANTITY")
	private Integer quantity;
	@Size(max = 1)
        @Column(name = "PRODTYPE")
	private String prodtype;
	@Lob
        @Column(name = "PICTURE")
	private Serializable picture;
	@Transient
	private boolean editable;
	@Transient
	private int cartCount;

	public int getCartCount() {
		return cartCount;
	}

	public void setCartCount(int cartCount) {
		this.cartCount = cartCount;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public Product() {
	}

	public Product(Integer prodid) {
		this.prodid = prodid;
	}

	public Integer getProdid() {
		return prodid;
	}

	public void setProdid(Integer prodid) {
		this.prodid = prodid;
	}

	public String getProdname() {
		return prodname;
	}

	public void setProdname(String prodname) {
		this.prodname = prodname;
	}

	public Double getProdprice() {
		return prodprice;
	}

	public void setProdprice(Double prodprice) {
		this.prodprice = prodprice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProdtype() {
		return prodtype;
	}

	public void setProdtype(String prodtype) {
		this.prodtype = prodtype;
	}

	public Serializable getPicture() {
		return picture;
	}

	public void setPicture(Serializable picture) {
		this.picture = picture;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (prodid != null ? prodid.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Product)) {
			return false;
		}
		Product other = (Product) object;
		if ((this.prodid == null && other.prodid != null) || (this.prodid != null && !this.prodid.equals(other.prodid))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "edu.uco.schambers.Entity.Product[ prodid=" + prodid + " ]";
	}
	public double calcSub()
	{
		return this.prodprice * this.cartCount;
	}
	public abstract String getProductInfo();

	
}
