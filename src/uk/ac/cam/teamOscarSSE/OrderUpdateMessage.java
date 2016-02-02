package uk.ac.cam.teamOscarSSE;

// TODO (stella): rename class
public class OrderUpdateMessage extends OrderChangeMessage {
	int size;
	double price;

	OrderUpdateMessage(Order order, int size, double price) {
		super(ChangeType.UPDATE, order);
		this.size = size;
		this.price = price;
	}

	public int getSize() {
		return size;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return String.format("{'id':%d,'type':'%s','size':%d, 'price':%f} %s",
				order.getOrderNum(), type, size, price, order);
	}
}
