package com.geoslab.tracking.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

import gnu.crypto.mac.UMac32;

/**
 * Clase que abstrae las operaciones del hash
 * @author rafarn
 *
 */
public class UMAC {
	private UMac32 uMac32 = new UMac32();
	
	private static String MAC_KEY_MATERIAL = "gnu.crypto.mac.key.material";
	private static String NONCE_MATERIAL = "gnu.crypto.umac.nonce.material";
	
	private static String PRIVATE_KEY = "1234567890123456";
	private static String PRIVATE_NONCE = "MdJNusd1";
	
	public boolean checkHash(String message, String incomingHash){
		// TODO
		return true;
	}
	
	/**
	 * Crea el Hash y lo transforma a String con carácteres ASCII (7 bits)
	 * @param message: mensaje a partir del cual se obtiene el hash
	 * @return String: hash creado
	 */
	@SuppressWarnings("unchecked")
	public String createHash(String message){
		@SuppressWarnings("rawtypes")
		Map attributes = new HashMap();
		attributes.put(MAC_KEY_MATERIAL, PRIVATE_KEY.getBytes());
		attributes.put(NONCE_MATERIAL, PRIVATE_NONCE.getBytes());
		
		try {
			uMac32.init(attributes);
//			byte[] data = message.getBytes("UTF-8");
			byte[] data = message.getBytes("ASCII");
			uMac32.update(data, 0, message.length());
			byte[] result = uMac32.digest();
			String result_real_string = new String(result);
			char[] c = result_real_string.toCharArray();
		    byte[] b = new byte[c.length];
		    for (int i = 0; i < c.length; i++){
		    	b[i] = (byte)(c[i] & 0x007F);
		    }
		    return new String(b);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
