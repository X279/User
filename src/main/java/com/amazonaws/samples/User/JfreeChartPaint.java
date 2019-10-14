package com.amazonaws.samples.User;

import java.awt.Font;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class JfreeChartPaint {
	ChartPanel frame1;
	static TimeSeries timeSeries;
	public JfreeChartPaint(TimeSeries timeSeries,String title,String message) {
		// TODO Auto-generated constructor stub
		System.out.println("画图");
		this.timeSeries = timeSeries;
		XYDataset xyDataset = createDataset();
		JFreeChart jFreeChart = ChartFactory.createTimeSeriesChart(title,"日期",message,xyDataset,true,true,true);
		XYPlot xyPlot = (XYPlot)jFreeChart.getPlot();
		DateAxis dateAxis = (DateAxis)xyPlot.getDomainAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));
		frame1 = new ChartPanel(jFreeChart,true);
		dateAxis.setLabelFont(new Font("黑体",Font.BOLD,14));
		dateAxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));
		ValueAxis rangeAxis = xyPlot.getRangeAxis();
		rangeAxis.setLabelFont(new Font("黑体",Font.BOLD,15));
		jFreeChart.getLegend().setItemFont(new Font("黑体",Font.BOLD,15));
		jFreeChart.getTitle().setFont(new Font("宋体",Font.BOLD,20));
	}
	
	private static XYDataset createDataset() {
		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		timeSeriesCollection.addSeries(timeSeries);
		return timeSeriesCollection;
	}
	
	public ChartPanel getChartPanel()
	{
		return frame1;
	}
}
