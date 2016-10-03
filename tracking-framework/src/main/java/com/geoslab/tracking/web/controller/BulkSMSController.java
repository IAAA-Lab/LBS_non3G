package com.geoslab.tracking.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoslab.tracking.web.domain.Response;

@Controller
public class BulkSMSController {
	
	@RequestMapping("/bulksms/")
//	public @ResponseBody Response bulk(
	public String bulk(HttpServletRequest request,
    		@RequestParam(value="message", required=true) String message,
    		@RequestParam(value="sender", required=true) String sender){
		
		// Se reemplazan los valores hexadecimales por su caracter correspondiente
		message = message.replace("?" + String.valueOf((char) 0x28), "{");
		message = message.replace("?" + String.valueOf((char) 0x29), "}");
		message = message.replace("?" + String.valueOf((char) 0x3c), "[");
		message = message.replace("?" + String.valueOf((char) 0x3e), "]");
		
		System.out.println("Your Message : " + message);
		System.out.println("Sender is: " + sender);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			Response response = mapper.readValue(message, Response.class);
			System.out.println("forward:/node/?" + response.responseToParams() + "&phoneNumber=" + sender);
			String params = response.responseToParams();
			if (params == null)
				return "error";
			else
				return "forward:/node/?" + params + "&phoneNumber=" + sender;
		} catch (JsonParseException e) {
			e.printStackTrace();
			return "error";
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return "error";
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}
	}
}
