/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.ejb;

import edu.uco.schambers.Entity.Sharkrepellent;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Large
 */
@Stateless
public class SharkrepellentFacade extends AbstractFacade<Sharkrepellent> {
	@PersistenceContext(unitName = "termProjectPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SharkrepellentFacade() {
		super(Sharkrepellent.class);
	}
	
}
