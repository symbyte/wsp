/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers;

import edu.uco.schambers.Entity.Product;

/**
 *
 * @author Large
 */
public class DeletedProduct extends Product {
	private String productInfo;

	public void setProductInfo(String pInfo)
	{
		this.productInfo = pInfo;
	}
	@Override
	public String getProductInfo() {
		return productInfo;
	}
	
}
