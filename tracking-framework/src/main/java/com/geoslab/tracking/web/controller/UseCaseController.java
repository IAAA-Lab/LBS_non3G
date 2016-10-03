package com.geoslab.tracking.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UseCaseController {
	
	@RequestMapping("/usecase")
	public String index() {
		return "index";
    }
	
	@RequestMapping("/usecase/detail")
	public String detail(
			@RequestParam(value="nodeID", required=true) long nodeID) {
		return "detail";
    }
}
