package uk.ac.cam.teamOscarSSE;

/**
 * GodBot is all knowing and all powerful and controls the world.
 */
public class GodBot extends Bot implements Runnable {
	private static final String botName = "GodBot";

	private final double FLIP_PROBABILITY = 0.25;
	private final double liquidity_factor = 0.001;
	int flip_ctr = 0;
	private long price;
	private boolean priceUp;
	private double rate;

	public GodBot(Exchange exchange, Stock stock) {
		super(exchange, stock, botName);
		price = stock.getStockPrice();
		priceUp = rand.nextBoolean();
		rate = rand.nextDouble() / 500.0;
	}

	@Override
	public void sendOrders() {
		computeNewPrice();
		OrderBook ob = exchange.getOrderBook(stock.getSymbol());
		int numToSell = 0;
		for (BuyOrder bo : ob.buys) {
			if (bo.getPrice() > price * (1.0 + liquidity_factor)) {
				numToSell += bo.getShares();
			} else {
				break;
			}
		}
		if (numToSell > 0) {
			submitOrder(new SellOrder(stock, this, numToSell, price));
		} else {

		}

		submitOrder(new SellOrder(stock, this, stock.getStockQty() / 4, price));

		int numToBuy = 0;
		for (SellOrder so : ob.sells) {
			if (so.getPrice() < price * (1 - liquidity_factor)) {
				numToBuy += so.getShares();
			} else {
				break;
			}
		}
		if (numToBuy > 0) {
			submitOrder(new BuyOrder(stock, this, numToBuy, price));
		}

		submitOrder(new BuyOrder(stock, this, stock.getStockQty() / 4, price));
	}

	private void computeNewPrice() {
		if (rand.nextDouble() < FLIP_PROBABILITY) {
			priceUp = !priceUp;
			++flip_ctr;
			rate = rand.nextDouble() / 500.0;
		}

		price += (priceUp ? 1 : -1) * rate * price;
	}

	@Override
	public void run() {
		while (super.exchange.isOpen()) {
			try {
				Thread.sleep(rand.nextInt(150) + 25);
				this.sendOrders();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
