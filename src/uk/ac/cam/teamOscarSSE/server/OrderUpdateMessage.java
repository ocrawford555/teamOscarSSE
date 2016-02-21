package uk.ac.cam.teamOscarSSE.server;

public class OrderUpdateMessage extends OrderChangeMessage {
	int size;
	long price;

	OrderUpdateMessage(Order order, int size, long price) {
		super(ChangeType.UPDATE, order);
		this.size = size;
		this.price = price;
	}

	public int getSize() {
		return size;
	}

	public long getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return String.format("{'id':%d,'type':'%s','size':%d, 'price':%d} %s",
				order.getOrderNum(), type, size, price, order);
	}
}
