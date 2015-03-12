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
public class UsersFacade extends AbstractFacade<Users> {
	@PersistenceContext(unitName = "termProjectPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public UsersFacade() {
		super(Users.class);
	}
	public List<Users> findByUsername(String uname) {
		return em.createNamedQuery("Users.findByUsername", Users.class).setParameter("username", uname).getResultList();
	}
	
}
