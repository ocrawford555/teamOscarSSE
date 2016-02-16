package uk.ac.cam.teamOscarSSE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

public class UserFrameServer implements Runnable {

	private Stock stock;
	//private String token;

	public void networkCom(String urlType, String urlParameters){
		URL url;
		HttpURLConnection connection = null;  

		try {
			url = new URL("http://localhost:8080/" + urlType);
			connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", 
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + 
					Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (
					connection.getOutputStream ());
			wr.writeBytes (urlParameters);
			wr.flush ();
			wr.close ();

			//Get Response	
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer(); 
			while((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			System.out.println(response.toString());
			//JSONObject jsonObj = new JSONObject(response.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if(connection != null) {
				connection.disconnect(); 
			}
		}
	}

	public UserFrameServer(Stock s) throws IOException {
		stock = s;
		networkCom("register/","{\"name\": \"Oliver\", \"email\": \"Awesome\"}\n");
	}

	//Change these to reflect HTTP Server format and all the get methods

	private long stockPrice;
	private List<Long> pointAvg = new LinkedList<Long>();
	private long overallAvg;
	private List<Long> transactionAvg = new LinkedList<Long>();
	private List<Float> rateOfChange = new LinkedList<Float>();
	private long cash;


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
	public void submitBuyOrder() {}

	//TODO
	public void submitSellOrder() {}

	public void update() {
		stockPrice = stock.getStockPrice();

		pointAvg = stock.getPointAvg();

		overallAvg = stock.getOverallAverage();

		transactionAvg = stock.getTransactionAvg();

		rateOfChange = stock.getRateOfChange();

		//cash = stock.getCash();
	}

	@Override
	public void run() {
		//have an initial wait period until the server has
		//started and running.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		while(true){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			update();
			//TODO: get working!
			submitBuyOrder();
		}
	}
}