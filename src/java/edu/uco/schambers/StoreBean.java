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
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
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
	List<Product> productList;
	String watchedUser;
	@EJB
	BookFacade bookFacade;
	@EJB
	SharkrepellentFacade sharkFacade;
	@EJB
	JetpackFacade jetpackFacade;
	@EJB
	ProductFacade productFacade;

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

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
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
		this.productList = productFacade.findAll();
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void addBook() {
		Book b = new Book();
		b.setEditable(true);
		this.bookList.add(b);
		this.productList.add(b);
	}

	public void addShark() {
		Sharkrepellent s = new Sharkrepellent();
		s.setEditable(true);
		this.sharkList.add(s);
		this.productList.add(s);
	}

	public void addJetpack() {
		Jetpack j = new Jetpack();
		j.setEditable(true);
		this.jetpackList.add(j);
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
		cart = new ArrayList<Product>();
	}

	public void addItem(Product item) {
		boolean found = false;
		for (Product cartStuff : cart) {
			if (item.getProdid() == cartStuff.getProdid()) {
				cartStuff.setQuantity(cartStuff.getQuantity() + 1);
				found = true;
			}
		}
		if (!found) {
			cart.add(item);
			item.setQuantity(1);
		}
	}

	public void removeItem(Product item) {
		if (cart.contains(item)) {
			item.setQuantity(item.getQuantity() - 1);
		}
		if (item.getQuantity() < 1) {
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
				"insert into orderlist (total, username) values (?,?)", Statement.RETURN_GENERATED_KEYS
			);
			statement.setDouble(1, cartTotal);
			statement.setString(2, currUser);
			statement.executeUpdate();
			ResultSet results = statement.getGeneratedKeys();
			int key = -1;
			if (results.next()) {
				key = results.getInt(1);
			}
			if (key == -1) {
				throw new SQLException("Generated key not returned.");
			}
			for (Product b : cart) {
				statement = conn.prepareStatement(
					"insert into orders(parentorder, isbn, quantity) values(?,?,?)"
				);
				statement.setInt(1, key);
				statement.setInt(2, b.getIsbn());
				statement.setInt(3, b.getQuantity());
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
				PreparedStatement innerStatement = conn.prepareStatement(
					"Select book.title,book.author,book.price,orders.quantity"
					+ " from book inner join orders on orders.parentorder = ? and book.isbn = orders.isbn "
				);
				innerStatement.setInt(1, orderKey);
				ResultSet innerResults = innerStatement.executeQuery();
				List<Product> bookList = new ArrayList<>();
				while (innerResults.next()) {

					Book b = new Book(innerResults.getString("title"),
						innerResults.getString("author"),
						innerResults.getDouble("price"),
						innerResults.getInt("quantity")
					);
					bookList.add(b);
				}
				Order o = new Order();
				o.setId(orderKey);
				o.setTotal(orderTotal);
				o.setUser(orderUser);
				o.setProducts(bookList);
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
				if (bookFacade.find(b.getProdid()) == null) {
					bookFacade.create(b);
				} else {
					bookFacade.edit(b);
				}
				b.setEditable(false);

			}
		}
		for (Jetpack j : jetpackList) {
			if (j.isEditable()) {
				if (jetpackFacade.find(j.getProdid()) == null) {
					jetpackFacade.create(j);
				} else {
					jetpackFacade.edit(j);
				}
				j.setEditable(false);
			}
		}
		for (Sharkrepellent s : sharkList) {
			if (s.isEditable()) {
				if (sharkFacade.find(s.getProdid()) == null) {
					sharkFacade.create(s);
				} else {
					sharkFacade.edit(s);
				}
				s.setEditable(false);
			}
		}

	}

	public void deleteBooks() {
		for (Product p : productList) {
			if (p.isEditable()) {
				productFacade.remove(p);
			}
		}
	}

	public boolean anyEditableProducts() {
		boolean found = false;
		for (Product p : productList) {
			if (p.isEditable()) {
				found = true;
			}
		}
		return found;
	}

}
