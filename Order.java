package teamOscarSSE;

public abstract class Order{
	public static final int BUY = 1;
	public static final int SELL = 2;

	//stock whose order book this object will belong to
	private Stock stock;
	
	//indicates whether order is for buying or selling
	private int typeOfOrder;
	
	//identifying party who placed order
	private String id;
	
	//number of shares
	private int shares;
	
	//price that buyer/seller asking for
	private double price;
	
	//unique order number
	private int orderNum;
	
	//time of order
	private long time;

	public String getId() {
		return id;
	}

	public int getShares() {
		return shares;
	}

	public void setShares(int shares) {
		this.shares = shares;
	}

	public double getPrice() {
		return price;
	}

	public int getOrderNum() {
		return orderNum;
	}

	public long getTime() {
		return time;
	}

	public Stock getStock() {
		return stock;
	}

	public int getTypeOfOrder() {
		return typeOfOrder;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public void setTypeOfOrder(int typeOfOrder) {
		this.typeOfOrder = typeOfOrder;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}

	public void setTime(long time) {
		this.time = time;
	}
}