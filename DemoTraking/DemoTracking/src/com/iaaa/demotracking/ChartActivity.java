package com.iaaa.demotracking;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iaaa.sensors.SensorValue;

public class ChartActivity extends Activity {

	private GraphicalView mChart;
	private String mTitle = "Gráfico";
	private String mUnits = "Desconocido";



	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.chart_activity);
		ArrayList<SensorValue> valores = null;
		
		Bundle extras = getIntent().getExtras();
		String value ="";
		if (extras != null) {
		    value = extras.getString("type");
		}
		if(value.equals("pressure")){
			valores = com.iaaa.sensors.LastSensorRead.getInstance().getLastPressureValues();
		}
		else if (value.equals("light")) {
			valores = com.iaaa.sensors.LastSensorRead.getInstance().getLastLightValues();
		}
		else if (value.equals("magneticField")) {
			valores = com.iaaa.sensors.LastSensorRead.getInstance().getLastMagneticFieldValues();
		}
		drawChart(valores);

	}
	private void drawChart(ArrayList<SensorValue> valores){

		if(valores==null || valores.size()==0){
			Toast.makeText(this, "No hay valores disponibles", Toast.LENGTH_SHORT).show();
			return;
		}
		mUnits = valores.get(0).getSensorValueUnits();
		mTitle = valores.get(0).getSensorName();

		Date[] dt = new Date[valores.size()];
		for(int i=0;i<valores.size();i++){
			dt[i] = valores.get(i).getSensorValueTime();
		}

		double[] values = new double[valores.size()];
		for(int i=0;i<valores.size();i++){
			values[i] = valores.get(i).getSensorValue()[0];
		}

		// Creating TimeSeries for Views
		TimeSeries viewsSeries = new TimeSeries("Views");

		// Adding data to Visits and Views Series
		for(int i=0;i<dt.length;i++){
			viewsSeries.add(dt[i],values[i]);
		}

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();


		// Adding Visits Series to dataset
		dataset.addSeries(viewsSeries);


		// Creating XYSeriesRenderer to customize viewsSeries
		XYSeriesRenderer viewsRenderer = new XYSeriesRenderer();
		viewsRenderer.setColor(Color.RED);
		viewsRenderer.setPointStyle(PointStyle.SQUARE);
		viewsRenderer.setFillPoints(true);
		viewsRenderer.setLineWidth(5);
		viewsRenderer.setDisplayChartValues(true);


		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

		multiRenderer.setChartTitle(mTitle);
		multiRenderer.setXTitle("Días y hora");
		multiRenderer.setYTitle(mUnits);
		multiRenderer.setZoomButtonsVisible(true);
		multiRenderer.setBackgroundColor(Color.GRAY);
		multiRenderer.setBarWidth(5);
		multiRenderer.setMarginsColor(Color.WHITE);
		multiRenderer.setPointSize(6);
		multiRenderer.setLabelsColor(Color.BLUE);
		multiRenderer.setAxesColor(Color.BLACK);
		multiRenderer.setAxisTitleTextSize(30);
		multiRenderer.setChartTitleTextSize(30);
		multiRenderer.setXLabelsColor(Color.BLACK);
		multiRenderer.setYLabelsColor(0, Color.BLACK);
		multiRenderer.setLabelsTextSize(22);
		multiRenderer.setYLabelsAlign(Align.LEFT);
		multiRenderer.setMargins(new int[]{ 60, 40, 0, 40 });


		// Adding visitsRenderer and viewsRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
		// should be same
		multiRenderer.addSeriesRenderer(viewsRenderer);

		// Getting a reference to LinearLayout of the MainActivity Layout
		RelativeLayout chartContainer = (RelativeLayout) findViewById(R.id.chartactivity_container_layout);

		// Creating a Time Chart
		mChart = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), dataset, multiRenderer,"dd-MMM HH:mm");

		multiRenderer.setClickEnabled(true);
		multiRenderer.setSelectableBuffer(20);

		// Setting a click event listener for the graph
		mChart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Format formatter = new SimpleDateFormat("dd-MMM HH:mm");

				SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();

				if (seriesSelection != null) {
					int seriesIndex = seriesSelection.getSeriesIndex();
					String selectedSeries="Visits";
					if(seriesIndex==0)
						selectedSeries = mUnits;

					// Getting the clicked Date ( x value )
					long clickedDateSeconds = (long) seriesSelection.getXValue();
					Date clickedDate = new Date(clickedDateSeconds);
					String strDate = formatter.format(clickedDate);

					// Getting the y value
					Double amount = (Double) seriesSelection.getValue();

					// Displaying Toast Message
					Toast.makeText(
							getBaseContext(),
							selectedSeries + " on "  + strDate + " : " + amount ,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Adding the Line Chart to the LinearLayout
		chartContainer.addView(mChart);
	}


}