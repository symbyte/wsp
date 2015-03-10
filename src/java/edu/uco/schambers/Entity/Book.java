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
@DiscriminatorValue("B")
@Table(name = "BOOK")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "Book.findAll", query = "SELECT b FROM Book b"),
	@NamedQuery(name = "Book.findByProdid", query = "SELECT b FROM Book b WHERE b.prodid = :prodid"),
	@NamedQuery(name = "Book.findByPages", query = "SELECT b FROM Book b WHERE b.pages = :pages"),
	@NamedQuery(name = "Book.findByAuthor", query = "SELECT b FROM Book b WHERE b.author = :author")})
public class Book extends Product implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "PAGES")
	private Integer pages;
	@Size(max = 255)
        @Column(name = "AUTHOR")
	private String author;

	public Book() {
	}

	public Book(Integer prodid) {
		super(prodid);
	}



	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	
}
