package uk.ac.cam.teamOscarSSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.HTTP;
import org.json.JSONArray;

public class UserProcessor {
	
	/**
	 * Place a new buy order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage buy(Exchange exchange, Player user,
											String symbol, int qty,
											long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("success", stock != null);
		if (stock != null) {
			Order order = new BuyOrder(stock, user, qty, price);
			exchange.addOrder(order);
			resultBody.put("orderID", order.getOrderNum());
		}
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Place a new sell order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage sell(Exchange exchange, Player user,
											String symbol, int qty,
											long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("success", stock != null);
		if (stock != null) {
			Order order = new SellOrder(stock, user, qty, price);
			exchange.addOrder(order);
			resultBody.put("orderID", order.getOrderNum());
		}
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Place a new buy-to-cover order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage cover(Exchange exchange, Player user,
											String symbol, int qty,
											long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("success", stock != null);
		if (stock != null) {
			Order order = new BuyToCoverOrder(stock, user, qty, price);
			exchange.addOrder(order);
			resultBody.put("orderID", order.getOrderNum());
		}
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Place a new short order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage sellShort(Exchange exchange, Player user,
											String symbol, int qty,
											long price) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("success", stock != null);
		if (stock != null) {
			Order order = new ShortOrder(stock, user, qty, price);
			exchange.addOrder(order);
			resultBody.put("orderID", order.getOrderNum());
		}
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Get a list of currently open orders for the specified user
	 * @param exchange
	 * @param user
	 * @return
	 * A HTTPReturnMessage with a list of order-ids in the data
	 */
	private static HTTPReturnMessage orders(Exchange exchange, Player user) {
		//TODO
		return null;
	}
	
	/**
	 * Get the details of the specified order (order must be for the specified
	 * user)
	 * @param exchange
	 * @param user
	 * @param orderID
	 * @return
	 * A HTTPReturnMessage with the order details in the data
	 */
	private static HTTPReturnMessage orders(Exchange exchange, Player user,
											long orderID) {
		//TODO
		return null;
	}
	
	/**
	 * Cancel the specified order
	 * @param exchange
	 * @param user
	 * @param orderID
	 * @return
	 * A HTTPReturnMessage with success/failure in the data
	 */
	private static HTTPReturnMessage cancel(Exchange exchange, Player user,
											long orderID) {
		return null;
	}
	
	/**
	 * Get a list of all available stocks to trade
	 * @param exchange
	 * @return
	 * A HTTPReturnMessage with a list of available stock symbols in the data
	 */
	private static HTTPReturnMessage stocks(Exchange exchange) {
		Map<String, JSONArray> resultBody = 
				new HashMap<String, JSONArray>();
		resultBody.put("stocks", new JSONArray(exchange.getStockSymbols()));
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Get all open orders about the specified stock from the exchange
	 * @param exchange
	 * @param symbol
	 * @return
	 * A HTTPReturnMessage with all the details about the stock in the data
	 */
	private static HTTPReturnMessage orderbook(Exchange exchange,
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
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", true);
		resultMap.put("symbol", symbol);
		resultMap.put("buy", new JSONArray(buyjson));
		resultMap.put("sell", new JSONArray(selljson));
		return new HTTPReturnMessage(resultMap);
	}
	
	/**
	 * Get the detailed information about a stock from the exchange
	 * @param exchange
	 * @param symbol
	 * @return
	 * A HTTPReturnMessage with the stock details in the data
	 */
	private static HTTPReturnMessage stock(Exchange exchange, String symbol) {
		Stock stock = exchange.getStockForSymbol(symbol);
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("success", stock != null);
		if (stock != null) {
			resultBody.put("symbol", stock.getSymbol());
		}
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Get the portfolio details of the specified user
	 * @param user
	 * @return
	 * A HTTPReturnMessage with the portfolio details in the data
	 */
	private static HTTPReturnMessage portfolio(Player user) {
		Portfolio pf = user.getPortfoio();
		long balance = user.getBalance();
		JSONObject stockOwned = new JSONObject(
										new JSONObject(pf.getOwnedStock()));
		JSONObject stockBorrowed = new JSONObject(
										new JSONObject(pf.getBorrowedStock()));
		Map<String, Object> portfolioDetails = new HashMap<String, Object>();
		portfolioDetails.put("balance", balance);
		portfolioDetails.put("stockOwned", stockOwned);
		portfolioDetails.put("stockBorrowed", stockBorrowed);
		
		return new HTTPReturnMessage(portfolioDetails);
	}
	
	/**
	 * Get the leaderboard details of all users
	 * @param exchange
	 * @return
	 * A HTTPReturnMessage with the leaderboard data in the data
	 */
	private static HTTPReturnMessage leaderboard(Exchange exchange) {
		JSONArray players = new JSONArray();
		for (Player player : exchange.getPlayers()) {
			Map<String, Object> playerDetails = new HashMap<>();
			playerDetails.put("ID", player.getToken());
			playerDetails.put("name", player.getName());
			playerDetails.put("score", player.getBalance());
			players.put(playerDetails);
		}
		Map<String, Object> resultBody = 
				new HashMap<String, Object>();
		resultBody.put("elapsed time", exchange.getUptime());
		resultBody.put("players", players);
		return new HTTPReturnMessage(resultBody);
	}
	
	/**
	 * Register a new player on the exchange.
	 * @param exchange
	 * @param name
	 * @param email
	 * @return
	 * A HTTPReturnMessage with the user-id in the data
	 */
	private static HTTPReturnMessage registerUser(Exchange exchange,
													String name, String email) {
		HTTPReturnMessage result = null;

		//Create a new player and add to the exchange's database
		Player user = new Player(name, email);
		boolean success = exchange.addPlayer(user);
		
		if (success) {			
			Map<String, String> resultBodyMap = 
					new HashMap<String, String>();
			resultBodyMap.put("response", "User Created");
			resultBodyMap.put("user-token", user.getToken());
			result = new HTTPReturnMessage(resultBodyMap);
		} else { //Created user's token already exists.
				 //At the moment the token is random, so should really just
				 // try again to get a new token, but ideally the token
				 // should be based on the user's name and/or email
			     // Returns the token of the existing user, although maybe
			 	 // it shouldn't? TODO
			Map<String, String> resultHeaderMap =
					new HashMap<String, String>();
			resultHeaderMap.put("Status-Code", "409");
			resultHeaderMap.put("HTTP_Version", "HTTP/1.1");
			resultHeaderMap.put("Reason-Phrase", "Conflict");
			String resultHeader =
					HTTP.toString(new JSONObject(resultHeaderMap));
			
			Map<String, String> resultBodyMap = 
					new HashMap<String, String>();
			resultBodyMap.put("response", "User Not Created (Already Exists)");
			resultBodyMap.put("user-token", user.getToken());
			String resultBody = (new JSONObject(resultBodyMap)).toString();
			result = new HTTPReturnMessage(resultHeader, resultBody);
		}
		
		return result;
	}
	
	/**
	 * Using the user-id data of the request, get the player object from the
	 * exchange
	 * @param exchange
	 * @param request
	 * @return
	 */
	private static Player determinePlayer(Exchange exchange,
												HTTPDetails request) {
		JSONObject requestData = new JSONObject(request.getBody());
		String userToken = (String) requestData.get("user-token");
		return exchange.getPlayer(userToken);
	}
	
	public static HTTPReturnMessage Process(Exchange exchange,
			HTTPDetails request) {
		String uri = request.getURI();
		uri = uri.substring(7, uri.length()); //Remove 'http://'
		String[] splituri = uri.split("/");
		
		HTTPReturnMessage result = null;
		switch(splituri[1]) { //Determine which function to call
			case "buy":
			{
				Player user = determinePlayer(exchange, request);
				String symbol = splituri[2];
				int qty = Integer.getInteger(splituri[3]);
				long price = Long.getLong(splituri[4]);
				result = buy(exchange, user, symbol, qty, price);
			} break;
			case "sell":
			{
				Player user = determinePlayer(exchange, request);
				String symbol = splituri[2];
				int qty = Integer.getInteger(splituri[3]);
				long price = Long.getLong(splituri[4]);
				result = sell(exchange, user, symbol, qty, price);
			} break;
			case "cover":
			{
				Player user = determinePlayer(exchange, request);
				String symbol = splituri[2];
				int qty = Integer.getInteger(splituri[3]);
				long price = Long.getLong(splituri[4]);
				result = cover(exchange, user, symbol, qty, price);
			} break;
			case "short":
			{
				Player user = determinePlayer(exchange, request);
				String symbol = splituri[2];
				int qty = Integer.getInteger(splituri[3]);
				long price = Long.getLong(splituri[4]);
				result = sellShort(exchange, user, symbol, qty, price);
			} break;
			case "orders":
			{
				Player user = determinePlayer(exchange, request);
				if (splituri.length == 2) {
					// uri: /orders
					result = orders(exchange, user);
				} else {
					// uri: /orders/{id}
					long orderID = Long.getLong(splituri[2]);
					result = orders(exchange, user, orderID);
				}
			} break;
			case "cancel":
			{
				Player user = determinePlayer(exchange, request);
				long orderID = Long.getLong(splituri[3]);
				result = cancel(exchange, user, orderID);
			} break;
			case "stocks":
			{
				result = stocks(exchange);
			} break;
			case "orderbook":
			{
				result = orderbook(exchange, splituri[2]);
			} break;
			case "stock":
			{
				result = stock(exchange, splituri[2]);
			} break;
			case "portfolio":
			{
				Player user = determinePlayer(exchange, request);
				result = portfolio(user);
			} break;
			case "leaderboard":
			{
				result = leaderboard(exchange);
			} break;
			case "register":
			{
				//Get user name and email from request
				JSONObject json = new JSONObject(request.getBody())	;	
				String name = json.getString("name");
				String email = json.getString("email");
				result = registerUser(exchange, name, email);
			} break;
			default:
				Map<String, String> resultHeaderMap =
								new HashMap<String, String>();
				resultHeaderMap.put("Status-Code", "400");
				resultHeaderMap.put("HTTP_Version", "HTTP/1.1");
				resultHeaderMap.put("Reason-Phrase", "Bad Request");
				String resultHeader =
						HTTP.toString(new JSONObject(resultHeaderMap));
				Map<String, String> resultBodyMap =
								new HashMap<String, String>();
				resultBodyMap.put("Reponse", "Invalid URI");
				String resultBody = resultBodyMap.toString();
				result = new HTTPReturnMessage(resultHeader, resultBody);
				
		}
		
		return result;
	}
}
