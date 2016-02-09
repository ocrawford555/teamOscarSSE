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
	public LineChart_AWT( String applicationTitle , String chartTitle )
	{
		super(applicationTitle);
		JFreeChart lineChart = ChartFactory.createLineChart(
				chartTitle,
				"Price","Time",
				createDataset(),
				PlotOrientation.VERTICAL,
				true,true,false);

		CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
		ValueAxis yAxis = plot.getRangeAxis();
		yAxis.setRange(12000.0, 13500.0);

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
			for(long value: MainAlgoTest.exchange.prices){
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
				"Stock Movement of BP with Boom Bot.");

		chart.pack( );
		RefineryUtilities.centerFrameOnScreen( chart );
		chart.setVisible( true );
	}
}