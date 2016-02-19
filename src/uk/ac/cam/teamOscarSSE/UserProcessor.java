package uk.ac.cam.teamOscarSSE;

import com.sun.net.httpserver.Headers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProcessor {

	/**
	 * Place a new buy order on the exchange for the specified user
	 *
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static String buy(Exchange exchange, Player user,
										 String symbol, int qty,
										 long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> data =
				new HashMap<String, Object>();
		boolean success = stock != null;
		if (stock != null) {
			Order order = new BuyOrder(stock, user, qty, price);
			success = exchange.addOrder(order);
			data.put("orderID", order.getOrderNum());
		}
		data.put("success", success);
		return convertMapToJSONString(data);
	}

	/**
	 * Place a new sell order on the exchange for the specified user
	 *
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static String sell(Exchange exchange, Player user,
										  String symbol, int qty,
										  long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> data =
				new HashMap<String, Object>();
		boolean success = stock != null;
		data.put("success", stock != null);
		if (stock != null) {
			Order order = new SellOrder(stock, user, qty, price);
			success = exchange.addOrder(order);
			data.put("orderID", order.getOrderNum());
		}
		data.put("success", success);
		return convertMapToJSONString(data);
	}

	/**
	 * Get a list of currently open orders for the specified user
	 *
	 * @param exchange
	 * @param user
	 * @return A HTTPReturnMessage with a list of order-ids in the data
	 */
	private static String orders(Exchange exchange, Player user) {
		//TODO
		return null;
	}

	/**
	 * Get the details of the specified order (order must be for the specified
	 * user)
	 *
	 * @param exchange
	 * @param user
	 * @param orderID
	 * @return A HTTPReturnMessage with the order details in the data
	 */
	private static String orders(Exchange exchange, Player user,
											long orderID) {
		//TODO
		return null;
	}

	/**
	 * Cancel the specified order
	 *
	 * @param exchange
	 * @param user
	 * @param orderID
	 * @return A HTTPReturnMessage with success/failure in the data
	 */
	private static String cancel(Exchange exchange, Player user,
											long orderID) {
		Map<String, Object> data =
				new HashMap<String, Object>();
		data.put("success", exchange.removeOrder(orderID));
		return convertMapToJSONString(data);
	}

	/**
	 * Get a list of all available stocks to trade
	 *
	 * @param exchange
	 * @return A HTTPReturnMessage with a list of available stock symbols in the data
	 */
	private static String stocks(Exchange exchange) {
		Map<String, JSONArray> data =
				new HashMap<String, JSONArray>();
		data.put("stocks", new JSONArray(exchange.getStockSymbols()));
		return convertMapToJSONString(data);
	}

	/**
	 * Get all open orders about the specified stock from the exchange
	 *
	 * @param exchange
	 * @param symbol
	 * @return A HTTPReturnMessage with all the details about the stock in the data
	 */
	private static String orderbook(Exchange exchange,
											   String symbol) {
		OrderBook orderbook = exchange.getOrderBook(symbol);
		List<BuyOrder> buys = orderbook.buys;
		List<SellOrder> sells = orderbook.sells;
		List<JSONObject> buyjson = new ArrayList<JSONObject>();
		List<JSONObject> selljson = new ArrayList<JSONObject>();
		for (BuyOrder order : buys) {
			JSONObject orderObject = new JSONObject();
			orderObject.put("price", order.getPrice());
			orderObject.put("qty", order.getShares());
			buyjson.add(orderObject);
		}
		for (SellOrder order : sells) {
			JSONObject orderObject = new JSONObject();
			orderObject.put("price", order.getPrice());
			orderObject.put("qty", order.getShares());
			selljson.add(orderObject);
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("success", true);
		data.put("symbol", symbol);
		data.put("buy", new JSONArray(buyjson));
		data.put("sell", new JSONArray(selljson));
		return convertMapToJSONString(data);
	}

	/**
	 * Get the detailed information about a stock from the exchange
	 *
	 * @param exchange
	 * @param symbol
	 * @return A HTTPReturnMessage with the stock details in the data
	 */
	private static String stock(Exchange exchange, String symbol) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> data =
				new HashMap<String, Object>();
		data.put("success", stock != null);

		if (stock != null) {
			data.put("symbol", stock.getSymbol());
			data.put("price", stock.getStockPrice());
			data.put("pointAvg", stock.getPointAvg());
			data.put("overallAvg", stock.getOverallAverage());
			data.put("transactionAvg", stock.getTransactionAvg());
			data.put("rateOfChange", stock.getRateOfChange());
		}
		return convertMapToJSONString(data);
	}

	/**
	 * Get the portfolio details of the specified user
	 *
	 * @param user
	 * @return A HTTPReturnMessage with the portfolio details in the data
	 */
	private static String portfolio(Player user) {
		Portfolio pf = user.getPortfolio();
		long balance = user.getBalance();
		JSONObject stockOwned = new JSONObject(
				new JSONObject(pf.getOwnedStock()));
		JSONObject stockBorrowed = new JSONObject(
				new JSONObject(pf.getBorrowedStock()));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("balance", balance);
		data.put("stockOwned", stockOwned);
		data.put("stockBorrowed", stockBorrowed);
		return convertMapToJSONString(data);
	}

	/**
	 * Get the leaderboard details of all users
	 *
	 * @param exchange
	 * @return A HTTPReturnMessage with the leaderboard data in the data
	 */
	private static String leaderboard(Exchange exchange) {
		JSONArray players = new JSONArray();
		for (Player player : exchange.getPlayers()) {
			Map<String, Object> playerDetails = new HashMap<>();
			playerDetails.put("ID", player.getToken());
			playerDetails.put("name", player.getName());
			playerDetails.put("score", player.getBalance());
			players.put(playerDetails);
		}
		Map<String, Object> data =
				new HashMap<String, Object>();
		data.put("elapsed time", exchange.getUptime());
		data.put("remaining time", exchange.getRemainingTime());
		data.put("open", exchange.isOpen());
		data.put("players", players);
		return convertMapToJSONString(data);
	}

	/**
	 * Register a new player on the exchange.
	 *
	 * @param exchange
	 * @return A HTTPReturnMessage with the user-id in the data
	 */
	private static String registerUser(Exchange exchange, JSONObject requestData) {
		
		String name = (String) requestData.get("name");
		String email = (String) requestData.get("email");

		//Create a new player and add to the exchange's database
		Player user = new Player(name, email);
		boolean success = exchange.addPlayer(user);

		if (success) {
			Map<String, String> data =
					new HashMap<String, String>();
			data.put("response", "User Created");
			data.put("user-token", user.getToken());
			return convertMapToJSONString(data);
		} else { //Created user's token already exists.
			//At the moment the token is random, so should really just
			// try again to get a new token, but ideally the token
			// should be based on the user's name and/or email
			// Returns the token of the existing user, although maybe
			// it shouldn't? TODO
			return null;
		}
	}
	
	private static String monetaryMetrics(long cash, int maxBuy, int maxSell) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("cash", cash);
		data.put("max-can-buy", maxBuy);
		data.put("max-can-sell", maxSell);
		return convertMapToJSONString(data);
	}

	/**
	 * Using the user-id data of the request, get the player object from the
	 * exchange
	 *
	 * @param exchange
	 * @param requestData
	 * @return
	 */
	private static Player determinePlayer(Exchange exchange,
										  JSONObject requestData) {
		if (requestData == null) {
			return null;
		}
		String userToken = (String) requestData.get("user-token");
		return exchange.getPlayer(userToken);
	}
	
	private static String convertMapToJSONString (Map<String, ?> map) {
		return new JSONObject(map).toString();
	}
	
	public static String processRequest(Exchange exchange, URI uri, Headers headers, String body) throws MalformedInputException {
		String[] components = uri.toString().split("/");
		JSONObject data;
		try {
			data = new JSONObject(body);
		} catch (JSONException exception) {
			data = null;
		}
		
		if (components.length >= 2) {
			switch (components[1]) {
				case "buy": {
					Player user = determinePlayer(exchange, data);
					if (user == null) {
						return null;
					}
					String symbol = components[2];
					int qty = Integer.parseInt(components[3]);
					long price = Long.parseLong(components[4]);
					return buy(exchange, user, symbol, qty, price);
				}
				case "sell": {
					Player user = determinePlayer(exchange, data);
					if (user == null) {
						return null;
					}
					String symbol = components[2];
					int qty = Integer.parseInt(components[3]);
					long price = Long.parseLong(components[4]);
					return sell(exchange, user, symbol, qty, price);
				}
				case "orders": {
					Player user = determinePlayer(exchange, data);
					if (user == null) {
						return null;
					}
					if (components.length == 2) {
						return orders(exchange, user);
					} else {
						long orderID = Long.parseLong(components[2]);
						return orders(exchange, user, orderID);
					}
				}
				case "cancel": {
					Player user = determinePlayer(exchange, data);
					if (user == null) {
						return null;
					}
					long orderID = Long.parseLong(components[2]);
					return cancel(exchange, user, orderID);
				}
				case "stocks":
					return stocks(exchange);
				case "orderbook":
					return orderbook(exchange, components[2]);
				case "stock":
					return stock(exchange, components[2]);
				case "portfolio": {
					Player user = determinePlayer(exchange, data);
					return portfolio(user);
				}
				//Leaderboard case not used for now
				case "leaderboard":
					return leaderboard(exchange);
				case "register": {
					// Get user name and email from request
					return registerUser(exchange, data);
				}
				//Returns cash left for user and MaxBuy/MaxSell for specified company
				//"cash/TickerSym"?
				case "cash": {
					Player user = determinePlayer(exchange, data);
					String company = components[2];
					Stock s = exchange.getStockForSymbol(company);
					long c = user.returnCash();
					int maxBuy = user.maxCanBuy(s, s.getStockPrice());
					int maxSell = user.maxCanSell(s);
					return monetaryMetrics(c, maxBuy, maxSell);
				}
			}
		}
		return null;
	}
}
