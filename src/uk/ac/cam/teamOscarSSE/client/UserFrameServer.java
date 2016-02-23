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

	private String token;
	private String stockSym;
	private long stockPrice;
	private List<Long> pointAvg = new LinkedList<Long>();
	private long overallAvg;
	//random generator for testing purposes only
	//keep in until all methods have been implements
	private Random rand = new Random();
	//keep store of the order book
	private List<JSONObject> orderBuys;
	private List<JSONObject> orderSells;
	//keep local copy of orders sent to the exchange
	private TreeSet<Integer> buyOrders = new TreeSet<Integer>();
	private TreeSet<Integer> sellOrders = new TreeSet<Integer>();
	//Change these to reflect HTTP Server format and all the get methods
	private List<Long> transactionAvg = new LinkedList<Long>();
	private List<Float> rateOfChange = new LinkedList<Float>();
	private long cash;
	private int maxBuy;
	private int maxSell;

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

	protected final long getCash() {
		return cash;
	}

	protected final int getMaxBuy() {
		return maxBuy;
	}

	protected final int getMaxSell() {
		return maxSell;
	}

	/**
	 * Sends URL with the user token in the body.
	 *
	 * @param urlType
	 * @return
	 */
	private final String sendURLWithToken(String urlType, String type) {
		return networkCom(urlType, "{\"user-token\": " + token + "}\n",type);
	}

	/**
	 * Method for sending request and receiving response from server.
	 * Type indicates whether request is GET or POST.
	 * @param urlType
	 * @param urlParameters
	 * @param type
	 * @return
	 */
	private final String networkCom(String urlType, String urlParameters, String type) {
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

	protected boolean Buy() {
		return false;
	}

	protected int volumeToBuy() {
		return rand.nextInt(100) + 5;
	}

	protected long priceToBuy() {
		/*
		int addExtraToBuy = 1;
		if (cash > 10500000)
			addExtraToBuy = 5;
		return stockPrice + addExtraToBuy;
		*/
		return stockPrice;
	}

	protected long priceToSell() {
		/*
		int subExtraToSell = 1;
		if (cash < 9500000)
			subExtraToSell = 5;
		return stockPrice - subExtraToSell;
		*/
		return stockPrice;
	}

	protected boolean Sell() {
		return false;
	}

	protected int volumeToSell() {
		return rand.nextInt(100) + 5;
	}


	private final boolean submitBuyOrder() {
		if (!Buy()) {
			return false;
		}
		String url = String.join("/", "buy", stockSym, Integer.toString(volumeToBuy()), Long.toString(priceToBuy()));
		String ob = sendURLWithToken(url, "POST");
		JSONObject obj = new JSONObject(ob);
		String message = obj.getString("message");
		if (obj.getBoolean("success") == true) {
			buyOrders.add(obj.getInt("orderID"));
			System.out.println("Order submitted: " + message);

			return true;
		}
		System.out.println("Failed to submit order: " + message);
		return false;

	}

	private final boolean submitSellOrder() {
		if (!Sell()) {
			return false;
		}
		String url = String.join("/", "sell", stockSym, Integer.toString(volumeToSell()), Long.toString(priceToSell()));
		String ob = sendURLWithToken(url, "POST");
		JSONObject obj = new JSONObject(ob);
		String message = obj.getString("message");
		if (obj.getBoolean("success") == true) {
			sellOrders.add(obj.getInt("orderID"));
			System.out.println("Order submitted: " + message);
			return true;
		}
		System.out.println("Failed to submit order: " + message);
		return false;
	}

	private final void getMonetaryMetrics() {
		String url = "cash/BAML";
		String ob = networkCom(url,
				"{\"user-token\": " + token + "}\n", "GET");
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
	private final List<String> getStocks() {
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
	private final boolean updateStock(String symbol) {
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

	private final void update() {
		getMonetaryMetrics();
		updateStock(stockSym);
	}

	protected final long getBestBuyPrice() {
		if (orderBuys == null || orderBuys.size() == 0) {
			// TODO: Return something else?
			return stockPrice;
		}
		return orderBuys.get(0).getLong("price");
	}

	protected final long getBestSellPrice() {
		if (orderSells == null || orderSells.size() == 0) {
			return stockPrice;
		}
		return orderSells.get(0).getLong("price");
	}

	
	/**
	 * Obtain order book from the exchange. Updates two lists
	 * which player can then interpret in their own way.
	 */
	private final void obtainOrderBook() {
		String orderBook = networkCom("orderbook/BAML",
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
		//have an initial wait period until the server has
		//started and running.
		long startTime = System.currentTimeMillis();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

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
			//if (System.currentTimeMillis() - startTime > 31000) return;
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