package uk.ac.cam.teamOscarSSE;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;
import org.json.HTTP;
import org.json.JSONArray;

public class UserProcessor {
	
	/**
	 * Place a new buy order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param Symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage buy(Exchange exchange, Player user,
											String Symbol, int qty,
											long price) {
		//TODO
		return null;
	}
	
	/**
	 * Place a new sell order on the exchange for the specified user
	 * @param exchange
	 * @param user
	 * @param Symbol
	 * @param qty
	 * @param price
	 * @return
	 * A HTTPReturnMessage with success (and the relevant order-id) or failure
	 * (and accompanying reason)
	 */
	private static HTTPReturnMessage sell(Exchange exchange, Player user,
											String Symbol, int qty,
											long price) {
		//TODO
		return null;
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
		//TODO
		return null;
	}
	
	/**
	 * Get a list of all available stocks to trade
	 * @param exchange
	 * @return
	 * A HTTPReturnMessage with a list of available stock symbols in the data
	 */
	private static HTTPReturnMessage stocks(Exchange exchange) {
		Map<String, JSONArray> resultBodyMap = 
				new HashMap<String, JSONArray>();
		resultBodyMap.put("stocks", new JSONArray(exchange.getStockSymbols()));
		return new HTTPReturnMessage(resultBodyMap);
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
		//TODO
		return null;
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
		Map<String, Object> resultBodyMap = 
				new HashMap<String, Object>();
		resultBodyMap.put("success", stock != null);
		if (stock != null) {
			resultBodyMap.put("symbol", stock.getSymbol());
		}
		return new HTTPReturnMessage(resultBodyMap);
	}
	
	/**
	 * Get the portfolio details of the specified user
	 * @param exchange
	 * @param user
	 * @return
	 * A HTTPReturnMessage with the portfolio details in the data
	 */
	private static HTTPReturnMessage portfolio(Exchange exchange, Player user) {
		//TODO
		return null;
	}
	
	/**
	 * Get the leaderboard details of all users
	 * @param exchange
	 * @return
	 * A HTTPReturnMessage with the leaderboard data in the data
	 */
	private static HTTPReturnMessage leaderboard(Exchange exchange) {
		//TODO
		return null;
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
			Map<String, String> resultHeaderMap =
					new HashMap<String, String>();
			resultHeaderMap.put("Status-Code", "200");
			resultHeaderMap.put("HTTP_Version", "HTTP/1.1");
			resultHeaderMap.put("Reason-Phrase", "OK");
			String resultHeader =
					HTTP.toString(new JSONObject(resultHeaderMap));
			
			Map<String, String> resultBodyMap = 
					new HashMap<String, String>();
			resultBodyMap.put("Response", "User Created");
			resultBodyMap.put("User-token", user.getToken());
			String resultBody = (new JSONObject(resultBodyMap)).toString();
			result = new HTTPReturnMessage(resultHeader, resultBody);
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
			resultBodyMap.put("Response", "User Not Created (Already Exists)");
			resultBodyMap.put("User-token", user.getToken());
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
		//TODO
		//Parse request into JSONObject
		JSONObject requestData = new JSONObject(request.getBody());
		
		
		return null;
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
				result = buy(exchange, user, symbol, qty, price);
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
				result = portfolio(exchange, user);
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
