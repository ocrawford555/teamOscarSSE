package uk.ac.cam.teamOscarSSE;

import java.awt.Color;
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
 *  Display on a line graph the price of the stock
 *  over three rounds.
 */
public class LineChart_AWT extends ApplicationFrame
{
	private static final long serialVersionUID = 1L;
	private float highest = Long.MIN_VALUE;
	private float lowest = Long.MAX_VALUE;

	public LineChart_AWT( String applicationTitle , String chartTitle )
	{
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Time","Price",
				createDataset(),
				PlotOrientation.VERTICAL,
				true,true,false);

		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		plot.getRenderer().setSeriesPaint(2,Color.MAGENTA);
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(lowest-5, highest+5);

		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		setContentPane( chartPanel );
	}

	private DefaultCategoryDataset createDataset( )
	{
		Main_1502_Recession.main(null);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

		int i = 0;

		List<Long> graphData = Main_1502_Recession.prices;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Recession", String.valueOf(i));
			}
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Main_1502_Normal.main(null);

		i = 0;

		graphData = Main_1502_Normal.prices;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Normal", String.valueOf(i));
			}
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Main_1502_Boom.main(null);

		i = 0;

		graphData = Main_1502_Boom.prices;

		synchronized(graphData){
			for(long value: graphData){
				float valueNew = ((float)value) / 100;
				if(valueNew > highest) highest = valueNew;
				if(valueNew < lowest) lowest = valueNew;
				i++;
				dataset.addValue(valueNew, "Boom", String.valueOf(i));
			}
		}
		return dataset;
	}

	public static void main( String[ ] args ) 
	{
		LineChart_AWT chart = new LineChart_AWT(
				"stock_Prices" ,
				"Stock Movement of BAML.");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}