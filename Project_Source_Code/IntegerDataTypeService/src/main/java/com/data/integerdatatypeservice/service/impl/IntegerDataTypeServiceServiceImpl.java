package com.data.integerdatatypeservice.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.integerdatatypeservice.model.DataProfilerInfo;
import com.data.integerdatatypeservice.model.DataSetStats;
import com.data.integerdatatypeservice.model.DimensionInfoModel;
import com.data.integerdatatypeservice.model.Dimensions;
import com.data.integerdatatypeservice.model.ProfilingInfoModel;
import com.data.integerdatatypeservice.repository.IntegerDataTypeRepository;
import com.data.integerdatatypeservice.service.IntegerDataTypeServiceService;

@Service
@Transactional
public class IntegerDataTypeServiceServiceImpl implements IntegerDataTypeServiceService {

	@Autowired
	IntegerDataTypeRepository integerDataTypeRepository;

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public String getIntegerDataTypePrediction(int wreakingPercentage, String collectionName) {
		LOGGER.info("Inside getIntegerDataTypePrediction");
		wreakingPercentage = 20; // Hardcoded value for wreaking %

		int numberOfRecords = 100;

		// get header of the dataset
		List<String> columnHeader1 = new ArrayList<String>();
		columnHeader1.add("eq_site_limit");
		columnHeader1.add("county");
		columnHeader1.add("statecode");
		columnHeader1.add("Date");
		int indivisualWreakingCountForDimentions = (((wreakingPercentage / 4) * numberOfRecords) / 100);
		LinkedHashSet<String> datadimention = new LinkedHashSet<String>();

		LOGGER.info("indivisualWreakingCountForDimentions" + indivisualWreakingCountForDimentions);
		try {

			List<DataSetStats> dataSetStatsList = null;
			ProfilingInfoModel profilingInfoModel = new ProfilingInfoModel();
			List<DataProfilerInfo> datasetStatsList = integerDataTypeRepository.findAll();
			DataProfilerInfo dataProfilerInfo = new DataProfilerInfo();

			/*
			 * int consistancyCnt = 0; int completenessCnt = 0; int accuracyCnt = 0; int
			 * validaityCnt = 0;
			 */

			for (int datasetHeadersIterator = 0; datasetHeadersIterator < columnHeader1
					.size(); datasetHeadersIterator++) {

				DimensionInfoModel dimensionInfoModel = new DimensionInfoModel();
				List<Dimensions> DimensionsList = new ArrayList<Dimensions>();

				int consistancyCnt = 0;
				int positiveValidityCnt = 0;
				int negativeValidityCnt = 0;
				int completenessCnt = 0;
				int accuracyCnt = 0;

				dataProfilerInfo = getDatasetThroughColumnName(collectionName,
						columnHeader1.get(datasetHeadersIterator), datasetStatsList);
				dataSetStatsList = dataProfilerInfo.getDatasetStats();
				for (int j = 0; j < dataSetStatsList.size(); j++) {
					if (dataSetStatsList.get(j).getColumnName().equals(columnHeader1.get(datasetHeadersIterator))) {
						profilingInfoModel = dataSetStatsList.get(j).getProfilingInfo();
					}
				}
				if (profilingInfoModel.getColumnDataType().equalsIgnoreCase("Integer")) {
					for (int patternIterator = 0; patternIterator < profilingInfoModel.getPatternsIdentified()
							.size(); patternIterator++) {
						LOGGER.info("Pattern = "
								+ profilingInfoModel.getPatternsIdentified().get(patternIterator).getPattern()
								+ ", Occurance = "
								+ profilingInfoModel.getPatternsIdentified().get(patternIterator).getOccurance());
						String patternString = profilingInfoModel.getPatternsIdentified().get(patternIterator)
								.getPattern();
						int patternValue = profilingInfoModel.getPatternsIdentified().get(patternIterator)
								.getOccurance();
						// null value present?
						if (patternString.equals("")) {
							completenessCnt = completenessCnt + patternValue;
							LOGGER.info("Completeness may be called");
						}
						// signed integer
						else if (patternString.matches("(?<=\\s|^)[-+]?\\d+(?=\\s|$)")) {
							if (patternString.matches("^\\d+$")) {
								positiveValidityCnt = positiveValidityCnt + patternValue;
							} else {
								negativeValidityCnt = negativeValidityCnt + patternValue;
							}
						}
						// float value with . or ,
						else if ((patternString.matches("^[-+]?\\d+(\\.\\d+)?$")
								|| patternString.matches("^[-+]?\\d+(\\,\\d+)?$"))
								&& (patternString.contains(".") || patternString.contains(","))) {
							consistancyCnt = consistancyCnt + patternValue;
							LOGGER.info("Consistancy for './,' may be called");
						} else {
							accuracyCnt = accuracyCnt + patternValue;
							LOGGER.info("Accuracy may be called");
						}
					}
					if (indivisualWreakingCountForDimentions > completenessCnt) {
						datadimention.add("Completeness");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("Completeness");
						dimensions.setReason("insufficient null values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - completenessCnt);
						DimensionsList.add(dimensions);
					}
					
					if (indivisualWreakingCountForDimentions > consistancyCnt) {
						datadimention.add("consistancy");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("consistancy");
						dimensions.setReason("insufficient decimal values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - consistancyCnt);
						DimensionsList.add(dimensions);
					}

					if (indivisualWreakingCountForDimentions > positiveValidityCnt) {
						datadimention.add("validaity");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("validaity");
						dimensions.setReason("insufficient +ve integer values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - positiveValidityCnt);
						DimensionsList.add(dimensions);
					}
					if (indivisualWreakingCountForDimentions > negativeValidityCnt) {
						datadimention.add("validaity");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("validaity");
						dimensions.setReason("insufficient -ve integer values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - negativeValidityCnt);
						DimensionsList.add(dimensions);
					}
					if (indivisualWreakingCountForDimentions > accuracyCnt) {
						datadimention.add("Accuracy");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("Accuracy");
						dimensions.setReason("insufficient accurate values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - accuracyCnt);
						DimensionsList.add(dimensions);
					}
					
					if(indivisualWreakingCountForDimentions > profilingInfoModel.getColumnStats().getDuplicateCount())
					{
						datadimention.add("Uniqueness");
						Dimensions dimensions = new Dimensions();
						dimensions.setDimensionName("Accuracy");
						dimensions.setReason("insufficient uniqueness values");
						dimensions.setStatus(true);
						dimensions.setRemainingWreakingCount(indivisualWreakingCountForDimentions - profilingInfoModel.getColumnStats().getDuplicateCount());
						DimensionsList.add(dimensions);
						
					}
				}

				dimensionInfoModel.setDimensionsList(DimensionsList);
				dataSetStatsList.get(datasetHeadersIterator).setDimensionList(dimensionInfoModel);
			}
			dataProfilerInfo.setDatasetStats(dataSetStatsList);
			integerDataTypeRepository.save(dataProfilerInfo);

		} catch (Exception e) {
			LOGGER.info("Exception " + e);
		}
		return "Success";
	}

	private DataProfilerInfo getDatasetThroughColumnName(String collectionName, String columnName,
			List<DataProfilerInfo> datasetStatsList) {
		DataProfilerInfo dataProfilerInfo = new DataProfilerInfo();
		for (int i = 0; i < datasetStatsList.size(); i++) {
			if (datasetStatsList.get(i).getFileName().equals(collectionName)) {
				dataProfilerInfo = datasetStatsList.get(i);
			}
		}

		return dataProfilerInfo;

	}

}
