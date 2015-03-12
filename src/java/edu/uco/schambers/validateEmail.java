/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uco.schambers;

import edu.uco.schambers.Entity.Users;
import edu.uco.schambers.ejb.UsersFacade;
import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author Steve
 */
@Named(value = "validateEmail")
@RequestScoped
public class validateEmail {

	@Inject
	UserBean userBean;
	private String key;
	private boolean valid;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void init() {

		valid = check(key); // And auto-login if valid?
		if (valid) {
			validateEmail();
		}
	}

	public boolean check(String k) {
		return k.equals(userBean.getEmailValidationKey());
	}

	public void validateEmail() {
		UsersFacade facade = userBean.getUsersFacade();
		Users u = facade.find(userBean.getIdOfEmailBeingValidated());
		u.setEmail(userBean.getEmailBeingValidated());
		facade.edit(u);
		try {

			FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
		} catch (IOException e) {
			//oh no!
		}

	}
}
