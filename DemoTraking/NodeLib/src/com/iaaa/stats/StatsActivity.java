package com.iaaa.stats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iaaa.appnode.R;

public class StatsActivity extends Activity {

	private Stats stats;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		stats = new Stats(this);
		refreshStats();

	}


	private void refreshStats(){
		//Rellenar los valores de las estadísticas
		int[] statsArray = stats.getStats();
		TextView sentMessages = (TextView) findViewById(R.id.sentMessagesText);
		TextView receivedMessages = (TextView) findViewById(R.id.receivedMessagesText);
		TextView sentHttp = (TextView) findViewById(R.id.sentHttpText);
		TextView receivedHttp = (TextView) findViewById(R.id.receivedHttpText);

		sentMessages.setText(statsArray[0]+"");
		receivedMessages.setText(statsArray[1]+"");
		sentHttp.setText(statsArray[2]+"");
		receivedHttp.setText(statsArray[3]+"");
	}

	/*
	 * Pone a 0 los contadores de estadísticas
	 */
	private void resetStats(){
		//Poner a 0 los TextView
		TextView sentMessages = (TextView) findViewById(R.id.sentMessagesText);
		TextView receivedMessages = (TextView) findViewById(R.id.receivedMessagesText);
		TextView sentHttp = (TextView) findViewById(R.id.sentHttpText);
		TextView receivedHttp = (TextView) findViewById(R.id.receivedHttpText);

		sentMessages.setText("0");
		receivedMessages.setText("0");
		sentHttp.setText("0");
		receivedHttp.setText("0");

		//Poner a 0 las estadísticas
		stats.resetStats();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
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
			refreshStats();
			return true;
		}
		else if (id == R.id.action_reset) {
			// Refrescar Puntos del mapa
			resetStats();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
