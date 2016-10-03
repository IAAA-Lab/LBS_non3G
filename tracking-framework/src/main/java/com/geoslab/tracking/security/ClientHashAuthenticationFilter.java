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

public class ClientHashAuthenticationFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		// El hash se calcula a partir de la URI SIN el parámetro de hash
		HttpServletRequest request = (HttpServletRequest) req;
		
		String hash = (String) request.getParameter("h");
		if (hash == null) {
			// Si no se incluye hash se bloquea la petición
			HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.sendRedirect("/auth_failed.html");
		}
		else {
			// Crea el string a partir del cual se genera el hash
			Enumeration<String> names = request.getParameterNames();
			String hashingText = "?";
			while(names.hasMoreElements()){
				String name = names.nextElement();
				String value = request.getParameter(name);
				if (!name.equals("h"))
					hashingText += name + "=" + value + "&";
			}
			// Elimina el último carácter (&)
			hashingText = hashingText.substring(0, hashingText.length()-1);
			
			// Crea el hash
			UMAC u = new UMAC();
			String result = u.createHash("hola");	// }Cx{}Y}q
			String result_2 = u.createHash(hashingText);
			System.out.println("Hash creado: " + result);
			System.out.println("Hash creado: " + result_2 + "a partir de: " + hashingText);
			
			if ("AA".equals(hash)) // FIXME
				// El hash es el de prueba, permitir
				chain.doFilter(req, res);
			else {
				if (result_2.equals(hash))
					// Hash correcto, seguir la cadena
					chain.doFilter(req, res);
				else {
					// Error con el hash
					HttpServletResponse httpResponse = (HttpServletResponse) res;
		            httpResponse.sendRedirect("/auth_failed.html");
				}
			}
		}
	}

	@Override
	public void destroy() {
		
	}

}
