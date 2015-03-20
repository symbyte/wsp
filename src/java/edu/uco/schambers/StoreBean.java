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
import edu.uco.schambers.Entity.Users;
import edu.uco.schambers.ejb.BookFacade;
import edu.uco.schambers.ejb.JetpackFacade;
import edu.uco.schambers.ejb.ProductFacade;
import edu.uco.schambers.ejb.SharkrepellentFacade;
import edu.uco.schambers.ejb.UsersFacade;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
	@EJB
	UsersFacade usersFacade;
	private Part part;
	private int imageToChange;
	private Users currUser;

	public int getImageToChange() {
		return imageToChange;
	}

	public void setImageToChange(int imageToChange) {
		this.imageToChange = imageToChange;
	}

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
				item.subtractQty();
				found = true;
			}
		}
		if (!found) {
			cart.add(item);
			item.setCartCount(1);
			item.subtractQty();
		}
	}

	public void removeItem(Product item) {
		if (cart.contains(item)) {
			item.setCartCount(item.getCartCount() - 1);
			item.addQty();
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
		Long currTime = System.currentTimeMillis();
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
			statement.setLong(3, currTime);
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
						"insert into orders(parentorder, prodid, quantity, description,price) values(?,?,?,?,?)"
				);
				statement.setInt(1, key);
				statement.setInt(2, p.getProdid());
				statement.setInt(3, p.getCartCount());
				statement.setString(4, p.getProductInfo());
				statement.setDouble(5, p.getProdprice());
				statement.executeUpdate();
				if (p instanceof Book) {
					bookFacade.edit((Book) p);
				} else if (p instanceof Sharkrepellent) {
					sharkFacade.edit((Sharkrepellent) p);
				} else if (p instanceof Jetpack) {
					jetpackFacade.edit((Jetpack) p);
				}
			}
			Order o = new Order();
			o.setUser(currUser);
			o.setOrderDate(currTime);
			o.setTotal(cartTotal);
			o.setProducts(cart);
			sendInvoice(o);
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
						"Select orders.prodid,product.prodtype,orders.quantity,orders.description, orders.price"
						+ " from orders left outer join product on product.prodid = orders.prodid where orders.parentorder = ?"
				);
				innerStatement.setInt(1, orderKey);
				ResultSet innerResults = innerStatement.executeQuery();
				List<Product> productList = new ArrayList<>();
				while (innerResults.next()) {

					int id = innerResults.getInt("prodid");
					String prodType = innerResults.getString("prodtype");
					int cartCount = innerResults.getInt("quantity");
					String desc = innerResults.getString("description");
					double price = innerResults.getDouble("price");
					Product p;
					DeletedProduct dp;
					if(prodType==null)
					{
						prodType = "deleted";
					}
					switch (prodType) {
						case "S":
							p = sharkFacade.find(id);
							p.setCartCount(cartCount);
							p.setProdprice(price);
							break;
						case "B":
							p = bookFacade.find(id);
							p.setCartCount(cartCount);
							p.setProdprice(price);
							break;
						case "J":
							p = jetpackFacade.find(id);
							p.setCartCount(cartCount);
							p.setProdprice(price);
							break;
						default:
							dp = new DeletedProduct();
							dp.setProductInfo(desc);
							dp.setProdprice(price);
							dp.setCartCount(cartCount);
							p = (Product) dp;
								
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

	public void uploadFile(int id) throws IOException, SQLException {

		FacesContext facesContext = FacesContext.getCurrentInstance();

		Connection conn = db.getConnection();

		InputStream inputStream;
		inputStream = null;
		try {
			inputStream = part.getInputStream();
			PreparedStatement insertQuery = conn.prepareStatement(
					"select count(*) from productImage where prodid=?");
			insertQuery.setInt(1, id);
			ResultSet results = insertQuery.executeQuery();
			int count = 0;
			if (results.next()) {

				count = results.getInt(1);
			}
			if (count == 0) {
				insertQuery = conn.prepareStatement("insert into productImage (prodid,file_name,file_type,file_size,file_contents)"
						+ " values(?,?,?,?,?)");
				insertQuery.setInt(1, id);
				insertQuery.setString(2, part.getSubmittedFileName());
				insertQuery.setString(3, part.getContentType());
				insertQuery.setLong(4, part.getSize());
				insertQuery.setBinaryStream(5, inputStream);
			} else {
				insertQuery = conn.prepareStatement("update productimage set file_name=?, file_type=?,file_size=?, "
						+ "file_contents=? where prodid=?");
				insertQuery.setString(1, part.getSubmittedFileName());
				insertQuery.setString(2, part.getContentType());
				insertQuery.setLong(3, part.getSize());
				insertQuery.setBinaryStream(4, inputStream);
				insertQuery.setInt(5, id);
			}

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

	public Part getPart() {
		return part;
	}

	public void setPart(Part part) {
		this.part = part;
	}

	public void changeImage(int id) {
		this.imageToChange = id;
	}

	public void sendInvoice(Order o) {
		final String username = "awesomestoretest@gmail.com";
		final String password = "t3st34man";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		String body = parseOrder(o);
		Session mailSession = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		try {
			String recip = usersFacade.findByUsername(o.getUser()).get(0).getEmail();
			Message message = new MimeMessage(mailSession);
			message.setFrom(new InternetAddress("awesomestore@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(recip));
			message.setSubject("Your Recent Order");
			message.setText(body);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	public String parseOrder(Order o) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		StringBuilder invoiceBody = new StringBuilder();
		invoiceBody.append("Hey ");
		invoiceBody.append(o.getUser());
		invoiceBody.append(",\n");
		invoiceBody.append("Here is the invoice for you most recent order:\n\n");
		for (Product p : o.getProducts()) {
			invoiceBody.append("Product: ");
			invoiceBody.append(p.getProductInfo());
			invoiceBody.append("\n");
			invoiceBody.append("Quantity: ");
			invoiceBody.append(p.getCartCount());
			invoiceBody.append("\n");
			invoiceBody.append("Price: ");
			invoiceBody.append(formatter.format(p.getProdprice()));
			invoiceBody.append("\n");
			invoiceBody.append("Subtotal: ");
			invoiceBody.append(formatter.format(p.calcSub()));
			invoiceBody.append("\n\n");
		}
		invoiceBody.append("Total: ");
		invoiceBody.append(formatter.format(o.getTotal()));
		invoiceBody.append("\n\n");
		invoiceBody.append("Thank you for your order!\nThe Store of Awesome Team");

		return invoiceBody.toString();
	}

}
