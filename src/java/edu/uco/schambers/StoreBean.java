package edu.uco.schambers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.uco.schambers.ejb.BookFacade;
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
	List<Book> cart;
	double cartTotal;
	List<Book> bookList;
	List<Order> orderList;
	String watchedUser;
	//@EJB
	//BookFacade bookFacade;

	public String getWatchedUser() {
		return watchedUser;
	}

	public String viewUserInfo(String watchedUser) {
		this.watchedUser = watchedUser;
		return null;
	}

	public double getCartTotal() {
		cartTotal = 0;
		for (Book item : cart) {
			cartTotal += item.calcSub();
		}
		return cartTotal;
	}

	public void getBookListFromDB() throws SQLException {
		if (db == null) {
			throw new SQLException("Database is null.");
		}
		Connection conn = db.getConnection();
		if (conn == null) {
			throw new SQLException("Connection is null.");
		}
		PreparedStatement statement = conn.prepareStatement(
			"select * from book"
		);
		ResultSet results = statement.executeQuery();
		List<Book> bookList = new ArrayList<>();
		try {
			while (results.next()) {
				Book b = new Book();
				b.setIsbn(results.getInt("isbn"));
				b.setTitle(results.getString("title"));
				b.setAuthor(results.getString("author"));
				b.setPrice(results.getDouble("price"));
				bookList.add(b);
			}

		} finally {
			conn.close();
		}
		this.bookList = bookList;
	}

	public List<Book> getBookList() {
		return bookList;
	}

	public void addBook() {
		Book b = new Book();
		b.setEditable(true);
		this.bookList.add(b);
	}

	public List<Book> getCart() {
		return cart;
	}

	public void setCart(List<Book> cart) {
		this.cart = cart;
	}

	@PostConstruct
	void init() {
		try {
			//getBookListFromDB();
			//getOrdersFromDB();
			//Book1 b = new Book1(9183);
			//b.setAuthor("the boss");
			//bookFacade.create(b);
			
		} catch (SQLException e) {
			throw new IllegalStateException("Something went wrong with DB.");
		}

		cart = new ArrayList<Book>();
	}

	public void addItem(Book item) {
		boolean found = false;
		for (Book cartStuff : cart) {
			if (item.getIsbn() == cartStuff.getIsbn()) {
				cartStuff.setQuantity(cartStuff.getQuantity() + 1);
				found = true;
			}
		}
		if (!found) {
			cart.add(item);
			item.setQuantity(1);
		}
	}

	public void removeItem(Book item) {
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
			for (Book b : cart) {
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
	public List<Order> getOrders(String user)
	{
		if(user.equals(""))
		{
			user = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
		}
		List<Order> orders = new ArrayList<>();
		for (Order o : this.orderList)
		{
			if(o.getUser().equals(user))
			{
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
				List<Book> bookList = new ArrayList<>();
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
				o.setBooks(bookList);
				orders.add(o);
			}

		} finally {
			conn.close();
		}
		this.orderList = orders;
	}

	public void updateBookDB() throws SQLException {
		if (db == null) {
			throw new SQLException("Database is null.");
		}
		Connection conn = db.getConnection();
		if (conn == null) {
			throw new SQLException("Connection is null.");
		}
		PreparedStatement update = conn.prepareStatement(
			"update book set title = ?, author = ?, price = ? where isbn = ?"
		);
		PreparedStatement insert = conn.prepareStatement(
			"insert into book (title, author, price, isbn) values(?,?,?,?)"
		);
		PreparedStatement query = conn.prepareStatement(
			"select Count(*)from book where isbn = ? or (title = ? and author = ? )"
		);
		try {
			for (Book b : bookList) {
				if (b.isEditable()) {
					query.setInt(1, b.getIsbn());
					query.setString(2,b.getTitle());
					query.setString(3,b.getAuthor());
					ResultSet rs = query.executeQuery();
					int recCount = -1;
					while (rs.next()) {
						recCount = rs.getInt(1);
					}
					if (recCount == 0) {
						insert.setString(1, b.getTitle());
						insert.setString(2, b.getAuthor());
						insert.setDouble(3, b.getPrice());
						insert.setInt(4, b.getIsbn());
						insert.execute();
					} else if (recCount > 0) {

						update.setString(1, b.getTitle());
						update.setString(2, b.getAuthor());
						update.setDouble(3, b.getPrice());
						update.setInt(4, b.getIsbn());
						update.execute();
					}
					b.setEditable(false);

				}
			}

		} finally {
			conn.close();
		}
	}

	public void deleteBooks() throws SQLException {
		if (db == null) {
			throw new SQLException("Database is null.");
		}
		Connection conn = db.getConnection();
		if (conn == null) {
			throw new SQLException("Connection is null.");
		}
		PreparedStatement delete = conn.prepareStatement(
			"delete from book where isbn = ?"
		);
		try {
			List<Book> deletedBooks = new ArrayList<>();
			for (Book b : bookList) {
				if (b.isEditable()) {
					delete.setInt(1, b.getIsbn());
					delete.execute();
					deletedBooks.add(b);
				}

			}
			bookList.removeAll(deletedBooks);

		} finally {
			conn.close();
		}

	}

	public boolean anyEditableBooks() {
		boolean found = false;
		for (Book b : bookList) {
			if (b.isEditable()) {
				found = true;
			}
		}
		return found;
	}


}
