package uk.ac.cam.teamOscarSSE.server;

public class OrderChangeMessage {

	protected ChangeType type;
	protected String debugMessage;
	protected Order order;

	OrderChangeMessage(ChangeType type, Order order, String msg) {
		this.type = type;
		this.order = order;
		this.debugMessage = msg;
	}

	OrderChangeMessage(ChangeType type, Order order) {
		this(type, order, "");
	}

	public ChangeType getType() {
		return type;
	}

	public String getMessage() {
		return debugMessage;
	}

	@Override
	public String toString() {
		return String.format("{'id':%d,'type':'%s'} %s", order.getOrderNum(), type, order);
	}

	public static enum ChangeType {
		CANCEL,     /* Order removed from exchange */
		ACK,        /* Order acknowledged and added to exchange */
		FAIL,       /* Failed to add order to exchange */
		FILL,       /* Order filled and removed from exchange   TODO: NOT IMPLEMENTED */
		UPDATE,     /* Partial fil */
	}
}
