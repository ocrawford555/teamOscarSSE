package uk.ac.cam.teamOscarSSE.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UserFrameServer implements Runnable {

	// The user's unique token assigned by the server.
	private String token;

	// The symbol of the stock that is available on the exchange.
	private String stockSym;

	// The last stock price.
	private long stockPrice;

	// A list of moving averages of the stock.
	private List<Long> pointAvg = new LinkedList();

	// The average price of the stock.
	private long overallAvg;

	// The average price of the transactions of the stock.
	private List<Long> transactionAvg = new LinkedList();
	private List<Float> rateOfChange = new LinkedList();

	// The cash owned by the user.
	private long cash;

	// The maximum number of stocks the user can buy at stockPrice.
	private int maxBuy;

	// The maximum number of stocks the user can sell.
	private int maxSell;

	//random generator for testing purposes only
	//keep in until all methods have been implements
	private Random rand = new Random();

	//keep store of the order book
	private List<JSONObject> orderBuys = new ArrayList<JSONObject>();
	private List<JSONObject> orderSells = new ArrayList<JSONObject>();

	//keep local copy of orders sent to the exchange
	private TreeSet<Integer> buyOrders = new TreeSet<Integer>();
	private TreeSet<Integer> sellOrders = new TreeSet<Integer>();

	/**
	 * Initialise player to the game, and set the stock variable.
	 * Token obtained when constructor is called.
	 */
	public UserFrameServer(String name) {
		String register =
				networkCom("register/", "{\"name\": \"" + name + "\", \"email\": \"Awesome\"}\n", "POST");
		JSONObject reJ = new JSONObject(register);
		token = (String) reJ.get("user-token");
	}

	/**
	 * Returns the price of the stock.
	 * <p>
	 * The price of the stock changes after each matched order in the exchange
	 * depending on characteristics of the stock as well as volume traded.
	 *
	 * @return the price of the stock
	 */
	protected final long getStockPrice() {
		return stockPrice;
	}

	protected final long getPointAvg(int i) {
		if (i > 49 || i < 0) return 0;
		else return pointAvg.get(i);
	}

	protected final long getOverallAvg() {
		return overallAvg;
	}

	protected final long getTransactionAvg(int i) {
		if (i>49 || i<0) return 0;
		else return transactionAvg.get(i);
	}

	protected final float getRateOfChange(int i) {
		if (i>49 || i<0) return 0;
		else return rateOfChange.get(i);
	}

	/**
	 * Get cash owned by the user.
	 *
	 * A user is not allowed to submit BUY orders with insufficient cash.
	 * Some cash may be blocked by outstanding BUY orders, hence not all of the
	 * cash is available.
	 *
	 * @return the cash owned by the user.
	 */
	protected final long getCash() {
		return cash;
	}

	/**
	 * Gets the maximum number of stocks the user can buy at the stock price.
	 *
	 * @return the maximum number of stocks the user can buy at the stock price.
	 */
	protected final int getMaxBuy() {
		return maxBuy;
	}

	/**
	 * Returns the maximum number of stocks the user can sell.
	 *
	 * A user is only allowed to short (sell stocks they do not own) up to a
	 * certain limit.
	 *
	 * @return the maximum number of stocks the user can sell
	 */
	protected final int getMaxSell() {
		return maxSell;
	}

	/**
	 * Returns the best (highest) buy price on the orderbook.
	 *
	 * If no buy order exists, returns the stock price.
	 *
	 * @return the highest buy price on the orderbook.
	 */
	protected final long getBestBuyPrice() {
		if (orderBuys == null || orderBuys.size() == 0) {
			return stockPrice;
		}
		return orderBuys.get(0).getLong("price");

	}

	/**
	 * Returns the best (lowest) sell price on the orderbook.
	 *
	 * If no sell order exists, returns the stock price.
	 *
	 * @return the lowest sell price on the orderbook.
	 */
	protected final long getBestSellPrice() {
		if (orderSells == null || orderSells.size() == 0) {
			return stockPrice;
		}
		return orderSells.get(0).getLong("price");
	}

	/**
	 * Returns up to the top 5 buy prices on the orderbook.
	 *
	 * If there are less than 5 orders, then returns 0 for the remaining indices.
	 *
	 * @return the top 5 (at most) buy prices on the orderbook.
	 */
	protected final long[] topBuyPrices() {
		int min = Math.min(5,orderBuys.size());
		long[] top = {0,0,0,0,0};
		if(!orderBuys.isEmpty()){
			for(int i = 0; i<min; i++){
				top[i] = orderBuys.get(i).getLong("price");
			}
		}
		return top;
	}

	/**
	 * Returns up to the top 5 sell prices on the orderbook.
	 *
	 * If there are less than 5 orders, then returns 0 for the remaining indices.
	 *
	 * @return the top 5 (at most) sell prices on the orderbook.
	 */
	protected final long[] topSellPrices() {
		int min = Math.min(5,orderSells.size());
		long[] top = {0,0,0,0,0};
		if(!orderSells.isEmpty()){
			for(int i = 0; i<min; i++){
				top[i] = orderSells.get(i).getLong("price");
			}
		}
		return top;
	}

	/**
	 * Returns the size of the top 5 buy orders on the orderbook.
	 *
	 * If there are less than 5 buy orders, then returns 0 for the remaining indices.
	 *
	 * @return the size of the top 5 buy orders on the orderbook.
	 */
	protected final int[] topBuyQuant() {
		int min = Math.min(5,orderBuys.size());
		int[] top = {0,0,0,0,0};
		if(!orderBuys.isEmpty()){
			for(int i = 0; i<min; i++){
				top[i] = orderBuys.get(i).getInt("qty");
			}
		}
		return top;
	}

	/**
	 * Returns the size of the top 5 sell orders on the orderbook.
	 *
	 * If there are less than 5 sell orders, then returns 0 for the remaining indices.
	 *
	 * @return the size of the top 5 sell orders on the orderbook.
	 */
	protected final int[] topSellQuant() {
		int min = Math.min(5,orderSells.size());
		int[] top = {0,0,0,0,0};
		if(!orderSells.isEmpty()){
			for(int i = 0; i<min; i++){
				top[i] = orderSells.get(i).getInt("qty");
			}
		}
		return top;
	}

	/**
	 * Sends request to the server with the user token in the body.
	 * Type indicates whether request is GET or POST.
	 *
	 * @param url the URL to send
	 * @param type GET or POST
	 * @return the response from the server
	 */
	private String sendURLWithToken(String url, String type) {
		return networkCom(url, "{\"user-token\": " + token + "}\n",type);
	}

	/**
	 * Method for sending request and receiving response from server.
	 * Type indicates whether request is GET or POST.
	 *
	 * @param urlType the URL to second
	 * @param urlParameters the URL parameters
	 * @param type GET or POST
	 * @return the response from the server
	 */
	private String networkCom(String urlType, String urlParameters, String type) {
		URL url;
		HttpURLConnection connection = null;

		try {
			url = new URL("http://localhost:8080/" + urlType);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(type);
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" +
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			//Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	/**
	 * This method is called when determining whether to submit a buy order.
	 *
	 * If it returns true, then a buy order is submitted to the exchange.
	 *
	 * @return true if the user wants to submit a buy order to the exchange
	 */
	protected boolean Buy() {
		return false;
	}

	/**
	 * This method is called when submitting a buy order to the exchange.
	 * It determines the size of the buy order.
	 *
	 * @return the size of the buy order to be submitted to the exchange
	 */
	protected int volumeToBuy() {
		return rand.nextInt(400) + 5;
	}

	/**
	 * This method is called when submitting a buy order to the exchange.
	 * It specifies the price of the buy order.
	 *
	 * @return the price of the buy order to be submitted to the exchange
	 */
	protected long priceToBuy() {
		return stockPrice;
	}

	/**
	 * This method is called when submitting a sell order to the exchange.
	 * It specifies the price of the sell order.
	 *
	 * @return the price of the sell order to be submitted to the exchange
	 */
	protected long priceToSell() {
		return stockPrice;
	}

	/**
	 * This method is called when determining whether to submit a sell order.
	 *
	 * If it returns true, then a sell order is submitted to the exchange.
	 *
	 * @return true if the user wants to submit a sell order to the exchange
	 */
	protected boolean Sell() {
		return false;
	}

	/**
	 * This method is called when submitting a sell order to the exchange.
	 * It determines the size of the sell order.
	 *
	 * @return the size of the sell order to be submitted to the exchange
	 */
	protected int volumeToSell() {
		return rand.nextInt(400) + 5;
	}

	/**
	 * This method submits a buy order to the exchange.
	 *
	 * @return true if the order was accepted by the exchange
	 */
	private boolean submitBuyOrder() {
		if (!Buy()) {
			return false;
		}
		String url = String.join("/", "buy", stockSym, Integer.toString(volumeToBuy()), Long.toString(priceToBuy()));
		String ob = sendURLWithToken(url, "POST");
		JSONObject obj = new JSONObject(ob);
		String message = obj.getString("message");
		if (obj.getBoolean("success")) {
			buyOrders.add(obj.getInt("orderID"));
			System.out.println("Order submitted: " + message);

			return true;
		}
		System.out.println("Failed to submit order: " + message);
		return false;

	}

	/**
	 * This method submits a sell order to the exchange.
	 *
	 * @return true if the order was accepted by the exchange
	 */
	private boolean submitSellOrder() {
		if (!Sell()) {
			return false;
		}
		String url = String.join(
				"/", "sell", stockSym,
				Integer.toString(volumeToSell()),
				Long.toString(priceToSell()));
		String ob = sendURLWithToken(url, "POST");
		JSONObject obj = new JSONObject(ob);
		String message = obj.getString("message");
		if (obj.getBoolean("success")) {
			sellOrders.add(obj.getInt("orderID"));
			System.out.println("Order submitted: " + message);
			return true;
		}
		System.out.println("Failed to submit order: " + message);
		return false;
	}

	private void getMonetaryMetrics() {
		String url = "cash/" + stockSym;
		String ob = sendURLWithToken(url, "GET");

		//ob contains return String in JSON format
		JSONObject obj = new JSONObject(ob);
		cash = obj.getLong("cash");
		maxBuy = obj.getInt("max-can-buy");
		maxSell = obj.getInt("max-can-sell");
	}

	/**
	 * Returns the stock symbols available on the exchange.
	 *
	 * @return
	 */
	private List<String> getStocks() {
		String url = "stocks";
		String ob = sendURLWithToken(url, "GET");
		JSONObject reJ = new JSONObject(ob);
		JSONArray stockArray = (JSONArray) reJ.get("stocks");
		List<String> stocks = new ArrayList<String>();

		if (stockArray == null) {
			return stocks;
		}

		for (int i = 0; i < stockArray.length(); ++i) {
			stocks.add(stockArray.getString(i));
		}
		return stocks;
	}

	/**
	 * Updates stock and metrics for the stock symbol.
	 *
	 * @return
	 */
	private boolean updateStock(String symbol) {
		String url = "stock/" + symbol;
		String ob = sendURLWithToken(url, "GET");
		if (ob == null) return false;
		JSONObject reJ = new JSONObject(ob);
		//System.out.println(reJ);
		if (!reJ.getBoolean("success")) {
			return false;
		}

		stockPrice = reJ.getLong("price");
		overallAvg = reJ.getLong("overallAvg");

		transactionAvg.clear();
		JSONArray transactionArray = (JSONArray) reJ.get("transactionAvg");
		for (int i = 0; i < transactionArray.length(); ++i) {
			transactionAvg.add(transactionArray.getLong(i));
		}

		pointAvg.clear();
		JSONArray pointAvgArray = (JSONArray) reJ.get("pointAvg");
		for (int i = 0; i < pointAvgArray.length(); ++i) {
			pointAvg.add(pointAvgArray.getLong(i));
		}

		rateOfChange.clear();
		JSONArray rateChangeArray = (JSONArray) reJ.get("rateOfChange");
		for (int i = 0; i < rateChangeArray.length(); ++i) {
			rateOfChange.add((float) rateChangeArray.getDouble(i));
		}

		return true;
	}

	private void update() {
		getMonetaryMetrics();
		updateStock(stockSym);
	}


	/**
	 * Obtain order book from the exchange. Updates two lists
	 * which player can then interpret in their own way.
	 */
	private void obtainOrderBook() {
		String orderBook = networkCom("orderbook/" + stockSym,
				"{}\n", "GET");

		JSONObject obj = new JSONObject(orderBook);

		JSONArray buys = obj.getJSONArray("buy");
		List<JSONObject> listBuys = new ArrayList<JSONObject>();

		for(int i=0;i < buys.length();i++){
			listBuys.add(buys.getJSONObject(i));
		}

		JSONArray sells = obj.getJSONArray("sell");
		List<JSONObject> listSells = new ArrayList<JSONObject>();

		for(int i=0;i < sells.length();i++){
			listSells.add(sells.getJSONObject(i));
		}

		orderBuys = listBuys;
		orderSells = listSells;
	}

	@Override
	public final void run() {
		//long startTime = System.currentTimeMillis();

		// Get the stocks available on the exchange.
		List<String> stocks = getStocks();
		while (stocks.size() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			stocks = getStocks();
		}
		stockSym = stocks.get(0);


		while(true){
			//if (System.currentTimeMillis() - startTime > 46000) return;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			update();
			submitBuyOrder();
			submitSellOrder();
			obtainOrderBook();
		}
	}
}