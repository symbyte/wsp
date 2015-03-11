/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers;

import edu.uco.schambers.Entity.Book;
import edu.uco.schambers.Entity.Sharkrepellent;
import edu.uco.schambers.ejb.BookFacade;
import edu.uco.schambers.ejb.SharkrepellentFacade;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;

/**
 *
 * @author Large
 */
@Named(value = "testBean")
@SessionScoped
public class TestBean implements Serializable {

	@EJB
	BookFacade bookFacade;
	@EJB
	SharkrepellentFacade sharkFacade;

	/**
	 * Creates a new instance of TestBean
	 */
	public TestBean() {
	}
	public void insert()
	{
		Sharkrepellent s = new Sharkrepellent(124124);
		s.setEffectiveness("Very");
		s.setProdname("Dude's");
		s.setQuantity(500);
		s.setVolume(23);
		s.setProdprice(341.23);
		sharkFacade.create(s);
	}
	
}
