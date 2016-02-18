package uk.ac.cam.teamOscarSSE;

/**
 * A player is a "human user" trading on the exchange.
 */
public class Player extends Trader {

	// The maximum number of stocks a player can borrow via the "SellOrder" type.
	// This is used whilst Short is not yet implemented.
	private final int SHORT_LIMIT = 1000;

	private final long INITIAL_CASH = 10000000;

	public Player(String name, String emailAddress) {
		super(name, emailAddress);
		updateCash(INITIAL_CASH);
	}

	/**
	 * Returns the starting cash of the player.
	 *
	 * @return
	 */
	public long getStartingCash() {
		return INITIAL_CASH;
	}

	/**
	 * Returns the maximum amount of stock a Player can buy.
	 * This is (cash - cashBlocked) / price.
	 *
	 * @param stock
	 * @param price
	 * @return
	 */
	@Override
	public int maxCanBuy(Stock stock, long price) {
		if (price == 0) {
			return 0;
		}
		return (int) ((cash - cashBlocked) / price);
	}

	/**
	 * Returns the maximum number of this stock a player can sell.
	 * This is equal to the amount of stock owned minus the amount blocked in pending orders.
	 *
	 * @param stock
	 * @return the maximum amount of stocks a Player can sell.
	 */
	@Override
	public int maxCanSell(Stock stock) {
		return pf.getAmountOwned(stock) - pending_pf.getAmountOwned(stock) + SHORT_LIMIT;
	}

	@Override
	public void reset() {
		super.reset();
		updateCash(INITIAL_CASH);
	}
}
