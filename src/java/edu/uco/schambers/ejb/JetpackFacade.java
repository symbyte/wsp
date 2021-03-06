/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.ejb;

import edu.uco.schambers.Entity.Jetpack;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Large
 */
@Stateless
public class JetpackFacade extends AbstractFacade<Jetpack> {
	@PersistenceContext(unitName = "termProjectPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public JetpackFacade() {
		super(Jetpack.class);
	}
	
}
