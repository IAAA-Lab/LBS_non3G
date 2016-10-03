package com.geoslab.tracking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.geoslab.tracking.security.ClientHashAuthenticationFilter;
import com.geoslab.tracking.security.NodeHashAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class MultiHttpSecurityConfig {
	
	@Configuration
	public static class BulkSMSWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	    	http
				.authorizeRequests()
					.antMatchers("/bulksms/**", "/usecase/**").permitAll();
	    }
	}
	
	@Configuration
	@Order(1)
	public static class NodeWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	    	http
				.antMatcher("/node**")
	    			.addFilterBefore(new NodeHashAuthenticationFilter(), BasicAuthenticationFilter.class);
	    		
	    }
	}
	
	@Configuration
	@Order(2)
	public static class ClientWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	    	http
				.antMatcher("/client/**")
	    			.addFilterBefore(new ClientHashAuthenticationFilter(), BasicAuthenticationFilter.class);
	    }
	}
}
