package uk.ac.cam.teamOscarSSE;

// TODO: extend Message
public class OrderChangeMessage {

	// TODO: subclass messages.
	public static enum ChangeType {
		CANCEL,     /* Order removed from exchange */
		ACK,        /* Order acknowledged and added to exchange */
		FAIL,       /* Failed to add order to exchange */
		FILL,       /* Order filled and removed from exchange   TODO: NOT IMPLEMENTED */
		UPDATE,     /* Partial fil */
	}

	protected ChangeType type;
	protected String debugMessage;
	protected Order order;

	// TODO: subclass and check what's needed, rather than passing entire order
	OrderChangeMessage(ChangeType type, Order order) {
		this.type = type;
		this.order = order;
	}

	@Override
	public String toString() {
		// TODO temporary format for testing
		return String.format("{'id':%d,'type':'%s'} %s", order.getOrderNum(), type, order);
	}
}
