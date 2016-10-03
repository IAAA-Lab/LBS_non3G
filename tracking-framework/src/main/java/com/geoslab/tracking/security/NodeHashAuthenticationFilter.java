package com.geoslab.tracking.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geoslab.tracking.web.domain.Response;

public class NodeHashAuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		// El hash se calcula a partir del mensaje en formato JSON
		HttpServletRequest request = (HttpServletRequest) req;
		
		String hash = (String) request.getParameter("h");
		if (hash == null) {
			// Si no se incluye hash se bloquea la petición
			HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.sendRedirect("/auth_failed.html");
		}
		else {
			// Crea un hashmap con los parámetros de la petición para crear el objeto Response
			HashMap<String, String> parameters = new HashMap<String, String>();
			Enumeration<String> names = request.getParameterNames();
			while(names.hasMoreElements()){
				String name = names.nextElement();
				String value = request.getParameter(name);
				parameters.put(name, value);
			}
			
			// Se crea la respuesta y se genera el string JSON
			Response response = new Response(parameters);
			String json = response.generateJSON();
			System.out.println("JSON: " + json);
			
			UMAC u = new UMAC();
			String result = u.createHash("hola");	// }Cx{}Y}q
			String result_2 = u.createHash(json);
			
			System.out.println("hash recibido: " + hash);
			System.out.println("hash creado: " + result_2);
			
			if ("AA".equals(hash)) // FIXME
				// El hash es el de prueba, permitir
				chain.doFilter(req, res);
			else {
				if (result_2.equals(hash))
					// Hash correcto, seguir la cadena
					chain.doFilter(req, res);
				else {
					// Error con el hash FIXME: cambiar el dofilter por el error
					HttpServletResponse httpResponse = (HttpServletResponse) res;
//		            httpResponse.sendRedirect("/auth_failed.html");
					chain.doFilter(req, res);
				}
			}
		}
	}

	@Override
	public void destroy() {
		
	}

}
