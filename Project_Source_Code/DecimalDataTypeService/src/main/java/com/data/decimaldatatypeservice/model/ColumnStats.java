package com.data.decimaldatatypeservice.model;

import java.time.LocalDate;
import java.util.List;



import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ColumnStats {
	int rowCount;
	int nullCount;
	List<String> distinctValueList;
	int distinctCount;
	List<FrequencyOfColumnValues> frequencyOfColumnValues;
	List<String> uniqueValuesList;
	List<String> duplicateValuesList;
	int uniqueCount;
	int duplicateCount;
	boolean isPrimaryKey;
	int maxLength;
	int minLength;
	int averageLength;
	int maxValue;
	int minValue;
	int averageValue;
	double minValueDecimal;
	double maxValueDecimal;
	double averageValueDecimal;
	String minDate;
	String maxDate;
	long trueCount;
	long falseCount;	
	MultiColumnStats multiColumnStats;
}
