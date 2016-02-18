package uk.ac.cam.teamOscarSSE;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *  Display on a line graph the balances of the players
 *  over one round.
 *  Currently only shows two players.
 */
public class LineChart_AWT2 extends ApplicationFrame
{
	private static final long serialVersionUID = 1L;
	private float highest = Long.MIN_VALUE;
	private float lowest = Long.MAX_VALUE;

	public LineChart_AWT2( String applicationTitle , String chartTitle )
	{
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Time","Balance",
				createDataset(),
				PlotOrientation.VERTICAL,
				true,true,false);

		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(lowest-10000, highest+10000);

		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		setContentPane( chartPanel );
	}

	private DefaultCategoryDataset createDataset( )
	{
		long PL1 = 0;
		long PL2 = 0;
		
		Main_1502_Normal.main(null);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

		int i = 0;

		List<Long> graphData = Main_1502_Normal.balA;
		List<Long> graphDataX = Main_1502_Normal.balB;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Alice/Nor", String.valueOf(i));
			}
		}

		i=0;

		synchronized(graphDataX){
			for(long value: graphDataX){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Bob/Nor", String.valueOf(i));
			}
		}
		
		try {	
			Thread.sleep(3000);
			System.out.println("");
			System.out.println("");
			System.out.println("STATS FROM ROUND 1 (Normal):");
			System.out.println("Final Stock Price: " + Main_1502_Normal.stocks.get(0).getStockPrice());
			PL1 += Main_1502_Normal.players.get(0).getBalance() - 
					Main_1502_Normal.players.get(0).getStartingCash();
			PL2 += Main_1502_Normal.players.get(1).getBalance() - 
					Main_1502_Normal.players.get(1).getStartingCash();
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Main_1502_Boom.main(null);

		i = 0;

		graphData = Main_1502_Boom.balA;
		graphDataX = Main_1502_Boom.balB;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Alice/Boom", String.valueOf(i));
			}
		}

		i=0;

		synchronized(graphDataX){
			for(long value: graphDataX){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Bob/Boom", String.valueOf(i));
			}
		}
		
		try {
			Thread.sleep(3000);
			System.out.println("");
			System.out.println("");
			System.out.println("STATS FROM ROUND 2 (Boom):");
			System.out.println("Final Stock Price: " + Main_1502_Boom.stocks.get(0).getStockPrice());
			PL1 += Main_1502_Boom.players.get(0).getBalance() - 
					Main_1502_Boom.players.get(0).getStartingCash();
			PL2 += Main_1502_Boom.players.get(1).getBalance() - 
					Main_1502_Boom.players.get(1).getStartingCash();
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Main_1502_Recession.main(null);

		i = 0;

		graphData = Main_1502_Recession.balA;
		graphDataX = Main_1502_Recession.balB;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Alice/Rec", String.valueOf(i));
			}
		}

		i=0;

		synchronized(graphDataX){
			for(long value: graphDataX){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Bob/Rec", String.valueOf(i));
			}
		}
		
		try {	
			Thread.sleep(3000);
			System.out.println("");
			System.out.println("");
			System.out.println("STATS FROM ROUND 3 (Recession):");
			System.out.println("Final Stock Price: " + Main_1502_Recession.stocks.get(0).getStockPrice());
			PL1 += Main_1502_Recession.players.get(0).getBalance() - 
					Main_1502_Recession.players.get(0).getStartingCash();
			PL2 += Main_1502_Recession.players.get(1).getBalance() - 
					Main_1502_Recession.players.get(1).getStartingCash();
			System.out.println("");
			System.out.println("");
			System.out.println("FINAL SCORES:");
			System.out.println("Alice: " + PL1);
			System.out.println("Bob: " + PL2);
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		return dataset;
	}

	public static void main( String[ ] args ) 
	{
		LineChart_AWT2 chart = new LineChart_AWT2(
				"stock_Prices" ,
				"Balance of Players at end of round.");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}