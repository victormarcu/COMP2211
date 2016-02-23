package ui.graphelements;

import java.awt.Dimension;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class PieChartElement implements ChartElement {

	private static int TITLE_HEIGHT = 30;
	private static int LEGEND_HEIGHT = 50;

	private PieChart chart;
	
	public PieChartElement(String chartTitle)
	{
		chart = new PieChart();
		chart.setTitle(chartTitle);
	}
	
	public void resizeChart(Dimension dimension)
	{
		chart.setPrefSize(dimension.getWidth(), dimension.getHeight() - TITLE_HEIGHT - LEGEND_HEIGHT);
	}
	
	/**
	 * Sets the data for the pie chart. 
	 * @param data Supplied as ObservableList of PieChart.Data elements, which ar
	 * a string and double pair.
	 */
	public void setData(ObservableList<PieChart.Data> data)
	{
		chart.setData(data);
	}
	
	public PieChart getChart()
	{
		return chart;
	}

}