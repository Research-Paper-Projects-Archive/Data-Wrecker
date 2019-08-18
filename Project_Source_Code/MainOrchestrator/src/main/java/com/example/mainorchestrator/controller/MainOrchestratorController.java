package com.example.mainorchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mainorchestrator.entity.DatasetDetails;
import com.example.mainorchestrator.service.CallDataWreckerOrchestratorService;
import com.example.mainorchestrator.service.LoadFileIntoMongo;

@RestController
@RequestMapping("/main_orchestrator")
public class MainOrchestratorController {

	@Autowired
	LoadFileIntoMongo loadFileService;
	
	@Autowired
	CallDataWreckerOrchestratorService dataWreckerOrchestratorService;
	
	private DatasetDetails datasetDetails;
	
	
	@GetMapping("/loadFileIntoMongo")
	public String loadFileIntoMongo() { 
		DatasetDetails datasetDetails = loadFileService.loadFileIntoMongo(); 
		String result = datasetDetails.getResult();
		if(result.equals("Success")) {
			result = loadFileService.callAllDataTypeDimensionServices(datasetDetails.getCollectionName(), 25);
		}
		return result;
	}
	
	@GetMapping("/random")
	public String callDataWreckerOrchestrator() {	
		datasetDetails = new DatasetDetails();
		datasetDetails.setCollectionName("testdatasetSample1");
		datasetDetails.setResult("rghnmmfmf");
		return dataWreckerOrchestratorService.callDataWreckerOrchestrator(datasetDetails);
	}
	
	
	
}
