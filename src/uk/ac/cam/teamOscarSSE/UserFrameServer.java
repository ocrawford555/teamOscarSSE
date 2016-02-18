package uk.ac.cam.teamOscarSSE;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserFrameServer implements Runnable {

	private Stock stock;
	private String token;
	private String stockSym;
	private long stockPrice;
	private List<Long> pointAvg = new LinkedList<Long>();
	private long overallAvg;

	//Change these to reflect HTTP Server format and all the get methods
	private List<Long> transactionAvg = new LinkedList<Long>();
	private List<Float> rateOfChange = new LinkedList<Float>();
	private long cash;

	public UserFrameServer(Stock s) throws IOException {
		stock = s;
		String register =
				networkCom("register/", "{\"name\": \"Oliver\", \"email\": \"Awesome\"}\n");
		JSONObject reJ = new JSONObject(register);
		token = (String) reJ.get("user-token");
	}

	/**
	 * Sends URL with the user token in the body.
	 *
	 * @param urlType
	 * @return
	 */
	public String sendURLWithToken(String urlType) {
		return networkCom(urlType, "{\"user-token\": " + token + "}\n");
	}

	public String networkCom(String urlType, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;

		try {
			url = new URL("http://localhost:8080/" + urlType);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
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

	public boolean Buy(){
		return false;
	}

	public int volumeToBuy() {
		return 0;
	}

	public boolean Sell(){
		return false;
	}

	public int volumeToSell() {
		return 0;
	}

	//TODO
	public void submitBuyOrder() {
		String url = "buy/BAML/40/" + (stockPrice + 2);
		String ob = networkCom(url,
				"{\"user-token\": " + token + "}\n");
		System.out.println(ob);
	}

	//TODO
	public void submitSellOrder() {
		String url = "sell/BAML/40/" + (stockPrice - 3);
		String ob = networkCom(url,
				"{\"user-token\": " + token + "}\n");
		System.out.println(ob);
	}

	/**
	 * Returns the stock symbols available on the exchange.
	 *
	 * @return
	 */
	public List<String> getStocks() {
		String url = "stocks";
		String ob = networkCom(url, "{\"user-token\": " + token + "}\n");
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
	 * Updates stock and metrics for the symbol.
	 *
	 * @return
	 */
	public boolean updateStock(String symbol) {
		String url = "stock/" + symbol;
		String ob = sendURLWithToken(url);
		// if (ob == null) return false;
		JSONObject reJ = new JSONObject(ob);
		System.out.println(reJ);
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

		// rateOfChange = stock.getRateOfChange();

		return true;
	}

	public void update() {
		update(stockSym);
	}

	public void update(String stockSymbol) {
		updateStock(stockSymbol);

		//cash = stock.getCash();
	}

	@Override
	public void run() {
		//have an initial wait period until the server has
		//started and running.
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Get the stocks available on the exchange.
		List<String> stocks = getStocks();
		while (stocks.size() == 0) {
			stocks = getStocks();
		}
		stockSym = stocks.get(0);


		while(true){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			update();
			submitBuyOrder();
			submitSellOrder();
			//obtainOrderBook();
		}
	}

	private void obtainOrderBook() {
		String ob = networkCom("orderbook/BAML",
				"{}\n");
		System.out.println(ob);
		//TODO do something with order book returned.
	}
}