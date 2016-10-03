package com.iaaa.demotracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.iaaa.location.LastLocation;
import com.iaaa.operations.OperationsFacade;
import com.iaaa.sensors.LastSensorRead;

public class MainActivity extends Activity implements ActionBar.TabListener {

	//Location Commands (Requests)
	final String LOCATION_GET_INFO = "LocationGetInfo";
	final String LOCATION_GET = "LocationGet";
	final String LOCATION_SET = "LocationSet";
	final String LOCATION_GET_REFRESH_RATE = "LocationGetRefreshRate";
	final String LOCATION_SET_REFRESH_RATE = "LocationSetRefreshRate";

	//Power Commands (Requests)
	final String POWER_GET_INFO = "PowerGetInfo";
	final String POWER_GET_LEVEL = "PowerGetLevel";

	//Device Commands (Requests)
	final String DEVICE_GET_INFO = "DeviceGetInfo";
	final String DEVICE_PING = "DevicePing";
	final String DEVICE_RESET = "DeviceReset";
	final String DEVICE_GET_MODE = "DeviceGetMode";
	final String DEVICE_SET_MODE = "DeviceSetMode";
	final String DEVICE_GET_SMS_CONFIG = "DeviceGetSMSConfig";
	final String DEVICE_SET_SMS_CONFIG = "DeviceSetSMSConfig";
	final String DEVICE_GET_HTTP_CONFIG = "DeviceGetHTTPConfig";
	final String DEVICE_SET_HTTP_CONFIG = "DeviceSetHTTPConfig";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//flushes logcat
		vaciarLogCat();
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
		.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}


		// Check device for Play Services APK.
		if (checkPlayServices()) {
			// If this check succeeds, proceed with normal processing.
			Log.i("APP_TEST", "Google play services: OK");
		}
		else {
			Toast.makeText(this, "Google play services no instalados, dispositivo no compatible",Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Se comprueban los servicios de Google
		checkPlayServices();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(new Intent(MainActivity.this,com.iaaa.message.MessageService.class));
		stopService(new Intent(MainActivity.this,com.iaaa.events.EventControllerService.class));
		stopService(new Intent(MainActivity.this,com.iaaa.modecontroller.ModeControllerService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_stats) {
			Intent stats = new Intent(this, com.iaaa.stats.StatsActivity.class);
			startActivity(stats);
			return true;
		}
		else if (id == R.id.action_config) {
			Intent i = new Intent(this, com.iaaa.appnode.SettingsActivity.class);
			startActivity(i);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return PlaceholderFragmentServer.newInstance(position + 1);			
			case 1:			
				return PlaceholderFragmentMain.newInstance(position + 1);
			default:
				return PlaceholderFragmentServer.newInstance(position + 1);	
			}

		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_servidor).toUpperCase(l);
				
			case 1:
				return getString(R.string.title_simulador).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragmentServer extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragmentServer newInstance(int sectionNumber) {
			PlaceholderFragmentServer fragment = new PlaceholderFragmentServer();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragmentServer() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_0, container,
					false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.text_respuestas);
			textView.setMovementMethod(new ScrollingMovementMethod());
			//textView.setText(Integer.toString(getArguments().getInt(
			//ARG_SECTION_NUMBER)));
			return rootView;
		}
	}


	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragmentMain extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragmentMain newInstance(int sectionNumber) {
			PlaceholderFragmentMain fragment = new PlaceholderFragmentMain();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragmentMain() {
		}


		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_2, container,
					false);
			//textView.setText(Integer.toString(getArguments().getInt(
			//ARG_SECTION_NUMBER)));
			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();
			refreshLastValues();
		}
		@Override
		public void onCreate (Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);

		}
		
		@Override
		public void onCreateOptionsMenu(
		      Menu menu, MenuInflater inflater) {
		   inflater.inflate(R.menu.main_fragment_actions, menu);
		}
		

		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_stats) {
				Intent stats = new Intent(getActivity(), com.iaaa.stats.StatsActivity.class);
				startActivity(stats);
				return true;
			}
			else if (id == R.id.action_config) {
				Intent i = new Intent(getActivity(), com.iaaa.appnode.SettingsActivity.class);
				startActivity(i);
				return true;
			}
			else if (id == R.id.action_refresh) {
				//Refrescar valores
				refreshLastValues();
				return true;
			}

			return super.onOptionsItemSelected(item);
		}

		public void refreshLastValues(){
			// Guardar referencia a los text views
			TextView latitudTextView = (TextView) getView().findViewById(R.id.text_ultima_localizacion_lat_value);
			TextView longitudTextView = (TextView) getView().findViewById(R.id.text_ultima_localizacion_long_value);
			TextView pressureTextView = (TextView) getView().findViewById(R.id.text_ultima_presion_value);
			TextView lightTextView = (TextView) getView().findViewById(R.id.text_ultima_luz_value);
			TextView magneticFieldTextView = (TextView) getView().findViewById(R.id.text_ultima_campomagnetico_value);

			//Declarar las variables donde se almacena el valor de los sensores
			String lastLat = "No disponible";
			String lastLong = "No disponible";
			String lastPressureValue = "No disponible";
			String lastLightValue = "No disponible";
			String lastMagneticFieldValue = "No disponible";


			//Rellenar las variables con el valor de los sensores

			if(LastLocation.getInstance().getLocation() != null){
				lastLat =  String.format("%.4f",LastLocation.getInstance().getLocation().getLatitude());
				lastLong =  String.format("%.4f",LastLocation.getInstance().getLocation().getLongitude());
			}

			if(LastSensorRead.getInstance().getLastPressureValues() != null && LastSensorRead.getInstance().getLastPressureValues().size() != 0){
				
				lastPressureValue =  String.format("%.2f", LastSensorRead.getInstance().getLastPressureValues().get(LastSensorRead.getInstance().getLastPressureValues().size()-1).getSensorValue()[0]);
			}
			if(LastSensorRead.getInstance().getLastLightValues() != null && LastSensorRead.getInstance().getLastLightValues().size() != 0){
				lastLightValue =  String.format("%.2f", LastSensorRead.getInstance().getLastLightValues().get(LastSensorRead.getInstance().getLastLightValues().size()-1).getSensorValue()[0]);
			}
			if(LastSensorRead.getInstance().getLastMagneticFieldValues() != null && LastSensorRead.getInstance().getLastMagneticFieldValues().size() != 0){
				lastMagneticFieldValue =  String.format("%.2f", LastSensorRead.getInstance().getLastMagneticFieldValues().get(LastSensorRead.getInstance().getLastMagneticFieldValues().size()-1).getSensorValue()[0]);
			}

			//Escribir el valor en los text views
			latitudTextView.setText(lastLat);
			longitudTextView.setText(lastLong);
			pressureTextView.setText(lastPressureValue + " hPA");
			lightTextView.setText(lastLightValue + " lux");
			magneticFieldTextView.setText(lastMagneticFieldValue + " µT");
		}

	}

	public void refreshLogCat(View view){
		try {
			TextView tv = (TextView)findViewById(R.id.text_operaciones_nodo);
			tv.setText("");
			Process process = Runtime.getRuntime().exec("logcat -d -v raw NodeExecution:D *:S");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));

			String line = "";
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				tv.append(line+"\n");
			}

		} 
		catch (IOException e) {e.printStackTrace();}

	}


	public void vaciarLogCat(){
		try {
			Runtime.getRuntime().exec("logcat -c");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void showMap(View view){
		Intent stats = new Intent(this, com.iaaa.demotracking.MapActivity.class);
		startActivity(stats);
	}
	public void showGraph(View view){
		Spinner spinner = (Spinner) findViewById(R.id.spinner_graficos);
		String grafico = String.valueOf(spinner.getSelectedItem());

		if(grafico.equals("Gráfico presión")){
			Intent charts = new Intent(this, com.iaaa.demotracking.ChartActivity.class);
			charts.putExtra("type", "pressure");
			startActivity(charts);
		}
		else if(grafico.equals("Gráfico luz")){
			Intent charts = new Intent(this, com.iaaa.demotracking.ChartActivity.class);
			charts.putExtra("type", "light");
			startActivity(charts);
		}
		else if(grafico.equals("Gráfico campo magnético")){
			Intent charts = new Intent(this, com.iaaa.demotracking.ChartActivity.class);
			charts.putExtra("type", "magneticField");
			startActivity(charts);
		}

	}


	public void executeOperation(View view){
		try{
			Spinner operationSpinner = (Spinner) findViewById(R.id.spinner_operaciones);

			OperationsFacade facade = new OperationsFacade();
			String result="";
			Context context=this;
			String commandName = String.valueOf(operationSpinner.getSelectedItem());
			JSONObject peticion=null;
			String peticionString ="";

			//Se llama a la operación dependiendo del comando del mensaje
			//Location commands

			if(commandName.equals(LOCATION_GET_INFO)){
				peticion = new JSONObject("{\"CMD\":\"LocationGetInfoReq\",\"h\":\"XX\"}");
				result = facade.locationGetInfo(peticion, context);
			}
			else if(commandName.equals(LOCATION_GET)){
				peticion = new JSONObject("{\"CMD\":\"LocationGetReq\",\"h\":\"XX\"}");
				result = facade.locationGet(peticion, context);
			}
			else if(commandName.equals(LOCATION_SET)){
				peticion = new JSONObject("{\"CMD\":\"LocationSetReq\",\"p\":[\"44.0\",\"N\",\"0.88\",\"E\"],\"h\":\"XX\"}");
				result = facade.locationSet(peticion, context);
			}
			else if(commandName.equals(LOCATION_GET_REFRESH_RATE)){
				peticion = new JSONObject("{\"CMD\":\"LocationGetRefreshRateReq\",\"h\":\"XX\"}");
				result = facade.locationGetRefreshRate(peticion, context);
			}
			else if(commandName.equals(LOCATION_SET_REFRESH_RATE)){
				peticion = new JSONObject("{\"CMD\":\"LocationSetRefreshRateReq\",\"p\":[\"15\"],\"h\":\"XX\"}");
				result = facade.locationSetRefreshRate(peticion, context);
			}
			//Power commands
			else if(commandName.equals(POWER_GET_INFO)){
				peticion = new JSONObject("{\"CMD\":\"PowerGetInfoReq\",\"h\":\"XX\"}");
				result = facade.powerGetInfo(peticion, context);
			}
			else if(commandName.equals(POWER_GET_LEVEL)){
				peticion = new JSONObject("{\"CMD\":\"PowerGetLevelReq\",\"h\":\"XX\"}");
				result = facade.powerGetLevel(peticion, context);
			}
			//Device commands
			else if(commandName.equals(DEVICE_GET_INFO)){
				peticion = new JSONObject("{\"CMD\":\"DeviceGetInfoReq\",\"h\":\"XX\"}");
				result = facade.deviceGetInfo(peticion, context);
			}
			else if(commandName.equals(DEVICE_PING)){
				peticion = new JSONObject("{\"CMD\":\"DevicePingReq\",\"h\":\"XX\"}");
				result = facade.devicePing(peticion, context);
			}
			else if(commandName.equals(DEVICE_RESET)){
				peticion = new JSONObject("{\"CMD\":\"DeviceResetReq\",\"h\":\"XX\"}");
				result = facade.deviceReset(peticion, context);
			}
			else if(commandName.equals(DEVICE_GET_MODE)){
				peticion = new JSONObject("{\"CMD\":\"DeviceGetModeReq\",\"h\":\"XX\"}");
				result = facade.deviceGetMode(peticion, context);
			}
			else if(commandName.equals(DEVICE_SET_MODE)){
				peticion = new JSONObject("{\"CMD\":\"DeviceSetModeReq\",\"p\":[\"4\"],\"h\":\"XX\"}");
				result = facade.deviceSetMode(peticion, context);
			}
			else if(commandName.equals(DEVICE_GET_SMS_CONFIG)){
				peticion = new JSONObject("{\"CMD\":\"DeviceSMSConfigReq\",\"h\":\"XX\"}");
				result = facade.deviceGetSMSConfig(peticion, context);
			}
			else if(commandName.equals(DEVICE_SET_SMS_CONFIG)){
				peticion = new JSONObject("{\"CMD\":\"DeviceSetSMSConfigReq\",\"h\":\"XX\",\"p\":[\"628481666\",\"15\",\"15\",\"15\"]}");
				result = facade.deviceSetSMSConfig(peticion, context);
			}
			else if(commandName.equals(DEVICE_GET_HTTP_CONFIG)){
				peticion = new JSONObject("{\"CMD\":\"DeviceHTTPConfigReq\",\"h\":\"XX\"}");
				result = facade.deviceGetHTTPConfig(peticion, context);
			}
			else if(commandName.equals(DEVICE_SET_HTTP_CONFIG)){
				peticion = new JSONObject("{\"CMD\":\"DeviceSetHTTPConfigReq\",\"h\":\"XX\",\"p\":[\"192.168.1.1\",\"15\",\"15\"]}");
				result = facade.deviceSetHTTPConfig(peticion, context);
			}
			escribirRespuesta(result);
			Log.d("hashprueba",result);
		}	
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void escribirRespuesta(String respuesta){
		TextView respuestaText = (TextView) findViewById(R.id.text_respuestas);
		respuestaText.append(respuesta+"\n\n");
	}

	public void iniciarProtocolo(View view){

		startService(new Intent(MainActivity.this,com.iaaa.message.MessageService.class));
		startService(new Intent(MainActivity.this,com.iaaa.events.EventControllerService.class));
		startService(new Intent(MainActivity.this,com.iaaa.modecontroller.ModeControllerService.class));
	}

	public void pararProtocolo (View view){
		stopService(new Intent(MainActivity.this,com.iaaa.message.MessageService.class));
		stopService(new Intent(MainActivity.this,com.iaaa.events.EventControllerService.class));
		stopService(new Intent(MainActivity.this,com.iaaa.modecontroller.ModeControllerService.class));
	}



	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i("APP_TEST", "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

}
