package com.iaaa.appnode;

import gnu.crypto.mac.UMac32;

import java.util.HashMap;
import java.util.Map;

public class HashCalculator {

	private UMac32 hash; 
	private final String MAC_KEY_MATERIAL = "gnu.crypto.mac.key.material";
	private final String NONCE_MATERIAL = "gnu.crypto.umac.nonce.material";
	private final String PRIVATE_KEY = "1234567890123456";
	private final String PRIVATE_NONCE = "MdJNusd1";


	public HashCalculator(){
		hash = new UMac32();
	}

	public String calculateHash(String message){
		try{
			System.out.println("HOLA");
			Map attributes = new HashMap();
			attributes.put(MAC_KEY_MATERIAL, PRIVATE_KEY.getBytes());
			attributes.put(NONCE_MATERIAL, PRIVATE_NONCE.getBytes());

			hash.init(attributes);

			byte[] data = message.getBytes("ASCII");
			hash.update(data, 0, message.length());
			byte[] result = hash.digest();
			String resultString =  new String(result, "ASCII");
			char[] c = resultString.toCharArray();
			byte[] b = new byte[c.length];
			for (int i = 0; i < c.length; i++){
				b[i] = (byte)(c[i] & 0x007F);
			}
			return new String(b, "ASCII");

		} catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}


}
