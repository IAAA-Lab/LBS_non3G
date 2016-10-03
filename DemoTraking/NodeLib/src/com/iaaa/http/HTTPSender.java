package com.iaaa.http;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Clase encargada de mandar las respuestas de las operaciones y eventos al 
 * servidor mediante HTTP
 * @author Ricardo Pallás
 *
 */
public class HTTPSender {

	private String domainName = "localhost"; //IP del servidor
	private String port = "9080"; //Puerto por defecto del servidor

	/**
	 * Construye un objeto HTTPSender para mandar peticiones http al servidor
	 * 
	 * @param domainName IP de la máquina (sin incluir http://)
	 * @param port Puerto de la máquina
	 */
	public HTTPSender(String domainName, String port){
		this.domainName = domainName;
		this.port = port;
	}
	public String sendHttp(String httpMessage){
		HttpTask httpTask = new HttpTask();
		httpTask.execute(httpMessage);
		return null;
	}

	/**
	 * Clase para lanzar la petición HTTP, extiendes AsyncTask 
	 * para que se lance en un hilo diferente, ya que Android no permite enviar
	 * peticiones Http en el thread principal de la UI.
	 * @author Ricardo Pallás
	 *
	 */
	private class HttpTask extends
	AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			try{
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				request.setURI(new URI("http://" + domainName + ":" + port + "/?" + params[0]));
				HttpResponse response = client.execute(request);
				Log.d("HTTP_SENDER", "Http command sent with params: "+ params[0]);
			}
			catch (Exception ex){
				ex.printStackTrace();
				Log.d("HTTP_SENDER", "Error while sending http command");
			}
			return null;


		}

	}

}


