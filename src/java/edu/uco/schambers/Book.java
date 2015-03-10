package edu.uco.schambers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Steve
 */
public class Book {
	private int isbn;
	private String title;
	private String author;
	private double price;
	private int quantity;
	private boolean editable = false;
	Book()
	{

	}
	Book(String t,String a,double p, int q)
	{
		this.title = t;
		this.author = a;
		this.price = p;
		this.quantity = q;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public int getIsbn() {
		return isbn;
	}

	public void setIsbn(int isbn) {
		this.isbn = isbn;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}
	public double calcSub()
	{
		return price * quantity;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
