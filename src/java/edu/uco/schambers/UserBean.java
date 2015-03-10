package edu.uco.schambers;

import edu.uco.schambers.Entity.Grouptable;
import edu.uco.schambers.Entity.Users;
import edu.uco.schambers.ejb.GrouptableFacade;
import edu.uco.schambers.ejb.UsersFacade;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Steven Chambers
 */
@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

	@Resource(name = "jdbc/prog7")
	DataSource db;
	@EJB
	private UsersFacade usersFacade;
	@EJB
	private GrouptableFacade grouptableFacade;

	List<Users> inMemoryUsers;

	@Size(max = 50, message = "Username must be less than 50 characters")
	@Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*", message = "Username must have letters and numbers only. Cannot begin with a number")
	private String username;

	@Size(max = 20, message = "First name must be shorter than 20 characters")
	@Pattern(regexp = "[a-zA-Z][a-zA-Z]*", message = "First name must have only alphabetical characters")
	private String firstName;
	@Size(max = 20, message = "Last name must be shorter than 20 characters")
	@Pattern(regexp = "[a-zA-Z][a-zA-Z]*", message = "Last name must have only alphabetical characters")
	private String lastName;
	@Size(min = 3, max = 50, message = "password must be between 3 and 50 digits long")
	private String password;

	@PostConstruct
	public void init() {
		inMemoryUsers = usersFacade.findAll();
		for (Users u : inMemoryUsers) {
			u.setAdmin(checkUserAdmin(u));
			u.setCust(checkUserCust(u));
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@Pattern(regexp = ".*@.*[.].*", message = "The address provided is not valid.")
	private String email;
	@Pattern(regexp = "^[1-9][0-9]{2}-[0-9]{3}-[0-9]{4}", message = "Phone number must be in format: XXX-XXX-XXXX")
	private String phoneNum;
	@Size(min = 1, message = "You must enter an address")
	private String address;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String signUp() throws SQLException {
		this.password = SHA256Encrypt.encrypt(password);
		Users user = new Users();
		user.setUsername(username);
		user.setPhonenum(phoneNum);
		user.setPassword(password);
		user.setLastname(lastName);
		user.setFirstname(firstName);
		user.setEmail(email);
		user.setAddress(address);
		usersFacade.create(user);
		Grouptable group = new Grouptable(username, "customergroup");
		grouptableFacade.create(group);
		inMemoryUsers.add(user);
		FacesContext context = FacesContext.getCurrentInstance();
		FacesMessage success = new FacesMessage("You have successfully signed up!");
		context.addMessage(null, success);
		return "/index";
	}

	public List<Users> getUsers() throws SQLException {

		return inMemoryUsers;
	}

	public boolean isLoggedIn() {
		String currUser;
		try {
			currUser = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	public boolean isCustomer() {
		return false;
	}

	public boolean anyEditableUsers() {
		boolean found = false;
		for (Users u : inMemoryUsers) {
			if (u.isEditable()) {
				found = true;
			}
		}
		return found;
	}

	public void updateUserDB() {
		for (Users u : inMemoryUsers) {
			if (u.isEditable()) {
				u.setEditable(false);
				if (u.getPassword().length() < 60) {
					u.setPassword(SHA256Encrypt.encrypt(u.getPassword()));
				}
				if (u.getId() == null) {
					usersFacade.create(u);
				} else {
					usersFacade.edit(u);
				}
				List<Grouptable> userGroups = grouptableFacade.findByUser(u);
				List<String> groupNames = new ArrayList<>();
				for (Grouptable gt : userGroups) {
					groupNames.add(gt.getGroupname());
				}
				if (u.isCust() && !checkUserCust(u)) {
					Grouptable g = new Grouptable(u.getUsername(), "customergroup");
					grouptableFacade.create(g);
				} else if (!u.isCust() && checkUserCust(u)) {
					for (Grouptable g : userGroups) {
						if (g.getGroupname().equals("customergroup")) {
							grouptableFacade.remove(g);
						}

					}
				}
				if (u.isAdmin() && !checkUserAdmin(u)) 
				{
					Grouptable g = new Grouptable(u.getUsername(), "admingroup");
					grouptableFacade.create(g);
				} 
				else if (!u.isAdmin() && checkUserAdmin(u)) 
				{
					for (Grouptable g : userGroups) 
					{
						if (g.getGroupname().equals("admingroup")) 
						{
							grouptableFacade.remove(g);
						}

					}
				}
			}
		}

	}

	public void deleteUsers() {
		List<Users> usersToRemove = new ArrayList<>();
		for (Users u : inMemoryUsers) {
			if (u.isEditable()) {
				usersFacade.remove(u);
				List<Grouptable> toRemove = grouptableFacade.findByUser(u);
				for (Grouptable g : toRemove) {
					grouptableFacade.remove(g);
				}
				usersToRemove.add(u);
			}
		}
		for (Users u : usersToRemove) {
			inMemoryUsers.remove(u);
		}

	}

	public void addUser() {
		Users u = new Users();
		u.setEditable(true);
		inMemoryUsers.add(u);
	}

	public boolean checkUserAdmin(Users u) {
		List<Grouptable> groups = grouptableFacade.findByUser(u);
		List<String> groupNames = new ArrayList<>();
		for (Grouptable gt : groups) {
			groupNames.add(gt.getGroupname());
		}
		return groupNames.contains("admingroup");

	}

	public boolean checkUserCust(Users u) {

		List<Grouptable> groups = grouptableFacade.findByUser(u);
		List<String> groupNames = new ArrayList<>();
		for (Grouptable gt : groups) {
			groupNames.add(gt.getGroupname());
		}
		return groupNames.contains("customergroup");
	}

}
