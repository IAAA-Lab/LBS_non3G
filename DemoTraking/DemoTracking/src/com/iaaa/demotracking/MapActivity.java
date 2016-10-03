package com.iaaa.demotracking;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.iaaa.demotracking.mapviewer.Facade;
import com.iaaa.demotracking.mapviewer.MapViewer;
import com.iaaa.location.LastLocation;

public class MapActivity extends MapViewer {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.map_activity);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.refresh, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			// Refrescar Puntos del mapa
			refreshMap();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void refreshMap() {
		Facade facade = getFacade();
		ArrayList<Point> points = new ArrayList<Point>();

		// Obtener las últimas localizaciones obetindas y trasnformalas a puntos

		ArrayList<Location> locations = LastLocation.getInstance()
				.getLocations();
		for (int i = 0; i < locations.size(); i++) {
			points.add(new Point(i, new Date(locations.get(i).getTime()),
					locations.get(i).getLatitude(), locations.get(i)
							.getLongitude()));

		}

		/*
		 * points.add(new Point(0, new Date(), 41.655517578125,
		 * -0.9054049849510193)); points.add(new Point(0, new Date(),
		 * 41.655517578125, -0.8854049849510193));
		 */

		try {
			facade.drawPointsAll(points, "EPSG:4326", this);

			facade.drawRoute(this, points, "1", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}