package edu.uco.schambers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.uco.schambers.Entity.Book;
import edu.uco.schambers.Entity.Jetpack;
import edu.uco.schambers.Entity.Product;
import edu.uco.schambers.Entity.Sharkrepellent;
import edu.uco.schambers.ejb.BookFacade;
import edu.uco.schambers.ejb.JetpackFacade;
import edu.uco.schambers.ejb.ProductFacade;
import edu.uco.schambers.ejb.SharkrepellentFacade;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.sql.DataSource;

@Named(value = "storeBean")
@SessionScoped
public class StoreBean implements Serializable {

	@Resource(name = "jdbc/prog7")
	DataSource db;
	List<Product> cart;
	double cartTotal;
	List<Book> bookList;
	List<Jetpack> jetpackList;
	List<Sharkrepellent> sharkList;
	List<Order> orderList;
	String watchedUser;
	@EJB
	BookFacade bookFacade;
	@EJB
	SharkrepellentFacade sharkFacade;
	@EJB
	JetpackFacade jetpackFacade;
	@EJB
	ProductFacade productFacade;
	private Part part;

	public List<Jetpack> getJetpackList() {
		return jetpackList;
	}

	public void setJetpackList(List<Jetpack> jetpackList) {
		this.jetpackList = jetpackList;
	}

	public List<Sharkrepellent> getSharkList() {
		return sharkList;
	}

	public void setSharkList(List<Sharkrepellent> sharkList) {
		this.sharkList = sharkList;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	public String getWatchedUser() {
		return watchedUser;
	}

	public String viewUserInfo(String watchedUser) {
		this.watchedUser = watchedUser;
		return null;
	}

	public double getCartTotal() {
		cartTotal = 0;
		for (Product item : cart) {
			cartTotal += item.calcSub();
		}
		return cartTotal;
	}

	public void getProductListFromDB() {
		this.bookList = bookFacade.findAll();
		this.jetpackList = jetpackFacade.findAll();
		this.sharkList = sharkFacade.findAll();
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void addBook() {
		Book b = new Book();
		b.setEditable(true);
		this.bookList.add(b);
	}

	public void addShark() {
		Sharkrepellent s = new Sharkrepellent();
		s.setEditable(true);
		this.sharkList.add(s);
	}

	public void addJetpack() {
		Jetpack j = new Jetpack();
		j.setEditable(true);
		this.jetpackList.add(j);
	}

	public List<Product> getCart() {
		return cart;
	}

	public void setCart(List<Product> cart) {
		this.cart = cart;
	}

	@PostConstruct
	void init() {

		getProductListFromDB();
		try {

			getOrdersFromDB();
		} catch (SQLException e) {
			throw new IllegalArgumentException("Something went wrong with fetching the orders");
		}
		cart = new ArrayList<Product>();
	}

	public void addItem(Product item) {
		boolean found = false;
		for (Product cartStuff : cart) {
			if (item.getProdid() == cartStuff.getProdid()) {
				cartStuff.setCartCount(cartStuff.getCartCount() + 1);
				found = true;
			}
		}
		if (!found) {
			cart.add(item);
			item.setCartCount(1);
		}
	}

	public void removeItem(Product item) {
		if (cart.contains(item)) {
			item.setCartCount(item.getCartCount() - 1);
		}
		if (item.getCartCount() < 1) {
			cart.remove(item);
		}
	}

	public boolean cartHasItems() {
		return !cart.isEmpty();
	}

	public boolean cartIsEmpty() {
		return cart.isEmpty();
	}

	public void checkOut() throws SQLException {
		if (db == null) {
			throw new SQLException("The database is null.");
		}
		Connection conn = db.getConnection();
		if (conn == null) {
			throw new SQLException("The connection is null");
		}
		try {
			String currUser = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
			PreparedStatement statement = conn.prepareStatement(
				"insert into orderlist (total, username,orderdate) values (?,?,?)", Statement.RETURN_GENERATED_KEYS
			);
			statement.setDouble(1, cartTotal);
			statement.setString(2, currUser);
			statement.setLong(3, System.currentTimeMillis());
			statement.executeUpdate();
			ResultSet results = statement.getGeneratedKeys();
			int key = -1;
			if (results.next()) {
				key = results.getInt(1);
			}
			if (key == -1) {
				throw new SQLException("Generated key not returned.");
			}
			for (Product p : cart) {
				statement = conn.prepareStatement(
					"insert into orders(parentorder, prodid, quantity) values(?,?,?)"
				);
				statement.setInt(1, key);
				statement.setInt(2, p.getProdid());
				statement.setInt(3, p.getCartCount());
				statement.executeUpdate();
			}
			cart.clear();

		} finally {
			conn.close();
		}

	}

	public List<Order> getOrders(String user) {
		if (user.equals("")) {
			user = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
		}
		List<Order> orders = new ArrayList<>();
		for (Order o : this.orderList) {
			if (o.getUser().equals(user)) {
				orders.add(o);
			}
		}
		return orders;
	}

	public void getOrdersFromDB() throws SQLException {
		if (db == null) {
			throw new SQLException("The database is null.");
		}
		Connection conn = db.getConnection();
		if (conn == null) {
			throw new SQLException("The connection is null");
		}
		List<Order> orders = new ArrayList<>();
		try {
			PreparedStatement statement = conn.prepareStatement(
				"select * from orderlist"
			);
			ResultSet results = statement.executeQuery();
			while (results.next()) {
				int orderKey = results.getInt("ordernumber");
				double orderTotal = results.getDouble("total");
				String orderUser = results.getString("username");
				Long millisDate = results.getLong("orderdate");
				PreparedStatement innerStatement = conn.prepareStatement(
					"Select product.prodid,product.prodtype,orders.quantity"
					+ " from product inner join orders on orders.parentorder = ? and product.prodid = orders.prodid"
				);
				innerStatement.setInt(1, orderKey);
				ResultSet innerResults = innerStatement.executeQuery();
				List<Product> productList = new ArrayList<>();
				while (innerResults.next()) {

					int id = innerResults.getInt("prodid");
					String prodType = innerResults.getString("prodtype");
					int cartCount = innerResults.getInt("quantity");
					Product p;
					switch (prodType) {
						case "S":
							p = sharkFacade.find(id);
							p.setCartCount(cartCount);
							break;
						case "B":
							p = bookFacade.find(id);
							p.setCartCount(cartCount);
							break;
						default:
							p = jetpackFacade.find(id);
							p.setCartCount(cartCount);
							break;
					}

					productList.add(p);
				}
				Order o = new Order();
				o.setId(orderKey);
				o.setTotal(orderTotal);
				o.setUser(orderUser);
				o.setOrderDate(millisDate);
				o.setProducts(productList);
				orders.add(o);
			}

		} finally {
			conn.close();
		}
		this.orderList = orders;
	}

	public void updateProductDB() {
		for (Book b : bookList) {
			if (b.isEditable()) {
				b.setEditable(false);
				if (bookFacade.find(b.getProdid()) == null) {
					bookFacade.create(b);
				} else {
					bookFacade.edit(b);
				}

			}
		}
		for (Jetpack j : jetpackList) {
			if (j.isEditable()) {
				j.setEditable(false);
				if (jetpackFacade.find(j.getProdid()) == null) {
					jetpackFacade.create(j);
				} else {
					jetpackFacade.edit(j);
				}
			}
		}
		for (Sharkrepellent s : sharkList) {
			if (s.isEditable()) {
				s.setEditable(false);
				if (sharkFacade.find(s.getProdid()) == null) {
					sharkFacade.create(s);
				} else {
					sharkFacade.edit(s);
				}
			}
		}

	}

	public void deleteProducts() {
		List<Book> booksToRemove = new ArrayList<>();
		for (Book b : bookList) {
			if (b.isEditable()) {
				bookFacade.remove(b);
				booksToRemove.add(b);
			}
		}
		bookList.removeAll(booksToRemove);
		List<Sharkrepellent> sharksToRemove = new ArrayList<>();
		for (Sharkrepellent s : sharkList) {
			if (s.isEditable()) {
				sharkFacade.remove(s);
				sharksToRemove.add(s);
			}
		}
		sharkList.removeAll(sharksToRemove);
		List<Jetpack> jetpacksToRemove = new ArrayList<>();
		for (Jetpack j : jetpackList) {
			if (j.isEditable()) {
				jetpackFacade.remove(j);
				jetpacksToRemove.add(j);
			}
		}
		jetpackList.removeAll(jetpacksToRemove);
	}

	public boolean anyEditableProducts() {
		boolean found = false;
		for (Book b : bookList) {
			if (b.isEditable()) {
				found = true;
			}
		}
		for (Sharkrepellent s : sharkList) {
			if (s.isEditable()) {
				found = true;
			}
		}
		for (Jetpack j : jetpackList) {
			if (j.isEditable()) {
				found = true;
			}
		}
		return found;
	}

	public String millisToDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/YYYY @ hh:mm aa");
		Date d = new Date(date);
		return formatter.format(d);

	}

	public void uploadFile() throws IOException, SQLException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		Connection conn = ds.getConnection();

		InputStream inputStream;
		inputStream = null;
		try {
			inputStream = part.getInputStream();
			PreparedStatement insertQuery = conn.prepareStatement(
				"INSERT INTO FILESTORAGE (FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_CONTENTS) "
				+ "VALUES (?,?,?,?)");
			insertQuery.setString(1, part.getSubmittedFileName());
			insertQuery.setString(2, part.getContentType());
			insertQuery.setLong(3, part.getSize());
			insertQuery.setBinaryStream(4, inputStream);

			int result = insertQuery.executeUpdate();
			if (result == 1) {
				facesContext.addMessage("uploadForm:upload",
					new FacesMessage(FacesMessage.SEVERITY_INFO,
						part.getSubmittedFileName()
						+ ": uploaded successfuly !!", null));
			} else {
				// if not 1, it must be an error.
				facesContext.addMessage("uploadForm:upload",
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
						result + " file uploaded", null));
			}
		} catch (IOException e) {
			facesContext.addMessage("uploadForm:upload",
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"File upload failed !!", null));
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public void validateFile(FacesContext ctx, UIComponent comp, Object value) {
		if (value == null) {
			throw new ValidatorException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"Select a file to upload", null));
		}
		Part file = (Part) value;
		long size = file.getSize();
		if (size <= 0) {
			throw new ValidatorException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"the file is empty", null));
		}
		if (size > 1024 * 1024 * 10) { // 10 MB limit
			throw new ValidatorException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR,
					size + "bytes: file too big (limit 10MB)", null));
		}
	}

}
