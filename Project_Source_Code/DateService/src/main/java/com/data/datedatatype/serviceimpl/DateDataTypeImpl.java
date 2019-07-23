package com.data.datedatatype.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.datedatatype.model.DatasetStats;
import com.data.datedatatype.model.Dimensions;
import com.data.datedatatype.model.PatternModel;
import com.data.datedatatype.service.DateDataTypeService;


@Service
@Transactional
public class DateDataTypeImpl implements DateDataTypeService{


	private Dimensions dimensions;

	@Override
	public Dimensions NullCheck(DatasetStats datasetStats, int wreckingPercentage) {
		
		dimensions = new Dimensions();
		int totalRowsCanBeWrecked = noOfRowsToBeWrecked(wreckingPercentage, datasetStats.getProfilingInfo().getColumnStats().getRowCount());
		
		if(datasetStats.getProfilingInfo().getColumnStats().getNullCount() > totalRowsCanBeWrecked) {
			dimensions.setDimensionName("NullCheck");
			dimensions.setStatus(false);
			dimensions.setReason("The number of null values exceeds threshold");
			return dimensions;
		} else {
			dimensions.setDimensionName("NullCheck");
			dimensions.setStatus(true);
			dimensions.setReason("The number of null values less than threshold");
			return dimensions;
		}
	}

	@Override
	public Dimensions ConsistencyCheck(DatasetStats datasetStats, int wreckingPercentage) {
		dimensions = new Dimensions();
		int totalRowsCanBeWrecked = noOfRowsToBeWrecked(wreckingPercentage, datasetStats.getProfilingInfo().getColumnStats().getRowCount());
		
		if(datasetStats.getProfilingInfo().getPatternsIdentified().size() > 1) {
			if(isConsistent(datasetStats,totalRowsCanBeWrecked)) {
				dimensions.setDimensionName("ConsistencyCheck");
				dimensions.setStatus(true);
				dimensions.setReason("The patterns identified are less than 20 in their occurances");
				return dimensions;
			}else {
				dimensions.setDimensionName("ConsistencyCheck");
				dimensions.setStatus(false);
				dimensions.setReason("The patterns identified are greater than 20 in their occurances");
				return dimensions;
			}
			
		}else {
			dimensions.setDimensionName("ConsistencyCheck");
			dimensions.setStatus(true);
			dimensions.setReason("The patterns identified are less than 20 in their occurances");
			return dimensions;
		}	
	}

	@Override
	public Dimensions ValidityCheck(DatasetStats datasetStats, int wreckingPercentage) {
		dimensions = new Dimensions();
		int totalRowsCanBeWrecked = noOfRowsToBeWrecked(wreckingPercentage, datasetStats.getProfilingInfo().getColumnStats().getRowCount());
		
		if(isValid(datasetStats,totalRowsCanBeWrecked)) {
			dimensions.setDimensionName("ValidityCheck");
			dimensions.setStatus(true);
			dimensions.setReason("There are valid values which is less than 20");
			return dimensions;
		}else {
			dimensions.setDimensionName("ValidityCheck");
			dimensions.setStatus(false);
			dimensions.setReason("There are Invalid values which is greater than 20");
			return dimensions;
		}
	}

	@Override
	public Dimensions AccuracyCheck(DatasetStats datasetStats, int wreckingPercentage) {
		dimensions = new Dimensions();
		dimensions.setDimensionName("AccuracyCheck");
		dimensions.setStatus(true);
		dimensions.setReason("There are Accurate values in the datasets");
		return dimensions;
		
	}
	
	private boolean isConsistent(DatasetStats datasetStats,int totalRowsCanBeWrecked) {
		int totalCount = 0;
		List<PatternModel> patternModelList = datasetStats.getProfilingInfo().getPatternsIdentified(); 
		ArrayList<Integer> regexCounts = new ArrayList<Integer>();
		for(int i=0; i< patternModelList.size(); i++) {			
			regexCounts.add(patternModelList.get(i).getOccurance());
			totalCount = totalCount + patternModelList.get(i).getOccurance();			
		}
		int maxValue = Collections.max(regexCounts);
		if((totalCount - maxValue) > 20) {
			return false;
		} else {
			return true;
		}
	}
	
	
	private boolean isValid(DatasetStats datasetStats,int totalRowsCanBeWrecked) {
			
		
		if(!(datasetStats.getProfilingInfo().getColumnStats().getMinLength() == datasetStats.getProfilingInfo().getColumnStats().getMaxLength() && 
				datasetStats.getProfilingInfo().getColumnStats().getMaxLength() == datasetStats.getProfilingInfo().getColumnStats().getAverageLength())) {
			int minLength = datasetStats.getProfilingInfo().getColumnStats().getMinLength();
			int maxLength = datasetStats.getProfilingInfo().getColumnStats().getMaxLength();
			int avgLength = datasetStats.getProfilingInfo().getColumnStats().getAverageLength();
			int maxValue = getMaxValue(minLength, maxLength, avgLength);
			
			if(maxValue == avgLength) {
				return isNotWrecked(minLength, maxLength,totalRowsCanBeWrecked);
			}else if(maxValue == maxLength){
				return isNotWrecked(minLength, avgLength,totalRowsCanBeWrecked);
			}else {
				return isNotWrecked(avgLength, maxLength,totalRowsCanBeWrecked);
			}
		}else {
			return true;
		}		
	}
	
	private int getMaxValue(int number1, int number2, int number3) {
		if(number1 > number2) {
			if(number1 > number3) {
				return number3;
			}else {
				return number1;
			}
		}else if(number2 > number3) {
			return number2;
		}else {
			return number3;
		}		
	}
	
	private boolean isNotWrecked(int number1, int number2,int totalRowsCanBeWrecked) {
		if((number1 + number2) > totalRowsCanBeWrecked) {
			return false;
		}else {
			return true;
		}
	}
	
	private int noOfRowsToBeWrecked(int wreckingPercentage, int rowCount) {
		
		int totalRowsCanBeWrecked = (wreckingPercentage * rowCount)/(100 * 4) ; 
		return totalRowsCanBeWrecked;
	}
	
	
}
