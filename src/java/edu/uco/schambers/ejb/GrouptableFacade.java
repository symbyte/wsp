/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers.ejb;

import edu.uco.schambers.Entity.Grouptable;
import edu.uco.schambers.Entity.Users;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Steve
 */
@Stateless
public class GrouptableFacade extends AbstractFacade<Grouptable> {

	@PersistenceContext(unitName = "termProjectPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public GrouptableFacade() {
		super(Grouptable.class);
	}

	public List<Grouptable> findByUser(Users u) {
		return em.createNamedQuery("Grouptable.findByUsername", Grouptable.class).setParameter("username", u.getUsername()).getResultList();
	}

}
