package com.iaaa.http;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Servicio que al ser ejecutado comprueba si está registrado en el servidor
 * para recibir notificaciones PUSH, y en caso contrario, lo registra y le manda
 * su identificación de Google Cloud Messaging al servidor para que éste le pueda
 * enviar notificaciones push.
 *  
 * @author Ricardo Pallás
 *
 */
public class HTTPService extends Service {
	

    public static final String PROPERTY_REG_ID = "registration_id"; //Para almacenar la id en las prefenrecias
    private static final String PROPERTY_APP_VERSION = "appVersion"; //Versión de la aplicación, por si cambia, volver a registrar
  
	String SENDER_ID = "Your-Sender-ID"; //El id del servidor
	static final String TAG = "HTTPService"; //Tag de depuración
	
	GoogleCloudMessaging gcm; //Instancia del objeto google cloud messaging
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs; //preferencias
    Context context; //contexto de la aplicación 

    String regid; //id del la app, para que el servidor pueda mandar notificaciones push
    
	@Override
	public void onCreate() {
		super.onCreate();
        context = getApplicationContext();

        //Comprobar la Play Services APK. Si la tiene, se procede con 
        // el registro en el GCM
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.length()==0) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        //Se para el servicio
        stopSelf();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();	
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	

	/**
	 * Comprueba que el dipositivo tiene la Google Play Services APK.
	 * Si no la tiene devuelve falso, si la tiene devuelve verdad.
	 * 
	 * @return Verdad si tiene la apk de Google Play Services, falso en caso contrario.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	            Log.i(TAG, "This device is not supported.");
	            System.exit(1);
	        
	        return false;
	    }
	    return true;
	}
	

	/**
	 * Obtiene la id de registro actual de la aplicación en el servicio GCM.
	 * @param context Contexto de la aplicación.
	 * <p>
	 * Si devuelve vacío, la app necesita registrarse.
	 * @return ID de registro, o String vacío si no existe.
	 */
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.length()==0) { 
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    return getSharedPreferences("GCMPreferences",
	            Context.MODE_PRIVATE);
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                
	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            Log.d(TAG, msg);
	        }
	    }.execute(null, null, null);
	}
	
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	
	
	
	
	

}
