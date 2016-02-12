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

public class LineChart_AWT extends ApplicationFrame
{
	private long highest = Long.MIN_VALUE;
	private long lowest = Long.MAX_VALUE;
	
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
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(lowest-400, highest+400);

		ChartPanel chartPanel = new ChartPanel( lineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		setContentPane( chartPanel );
	}

	private DefaultCategoryDataset createDataset( )
	{
		MainAlgoTest.main(null);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
		int i = 0;
		List<Long> graphData = MainAlgoTest.exchange.prices;
		synchronized(graphData){
			for(long value: graphData){
				if(value > highest) highest = value;
				if(value < lowest) lowest = value;
				i++;
				dataset.addValue(value, "prices", String.valueOf(i));
			}
		}
		return dataset;
	}
	public static void main( String[ ] args ) 
	{
		LineChart_AWT chart = new LineChart_AWT(
				"stock_Prices" ,
				"Stock Movement of BP.");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}