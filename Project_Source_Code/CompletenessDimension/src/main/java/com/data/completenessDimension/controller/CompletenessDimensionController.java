package com.data.completenessDimension.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.data.completenessDimension.service.CompletenessDimensionService;

@RestController
@RequestMapping("/dimension")
public class CompletenessDimensionController {

	@Autowired
	private CompletenessDimensionService completenessDimensionService;
	
	
	@RequestMapping(value = "/completenessDimension", method = RequestMethod.GET)
	public String applyCompletenessDimension(@RequestParam String collectionName, @RequestParam String columnName) {	
		return completenessDimensionService.removeValues();
	}
}