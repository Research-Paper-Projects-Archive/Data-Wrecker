package com.data.datawreakerinterface.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.datawreakerinterface.exception.DataWreakernterfaceException;
import com.data.datawreakerinterface.model.DataProfilerInfo;
import com.data.datawreakerinterface.model.DatasetDetails;
import com.data.datawreakerinterface.repository.DataProfilerInfoRepository;
import com.data.datawreakerinterface.service.DataWreakerIntefaceService;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

@Service
@Transactional
public class DataWreakerInterfaceServiceImpl implements DataWreakerIntefaceService {

	@Autowired
	private DataProfilerInfoRepository dataProfilerInfoRepo;
	private DatasetDetails dataSet;
	
	public DatasetDetails putCsvDataIntoMongo() throws DataWreakernterfaceException {
		dataSet = new DatasetDetails();
		Runtime r = Runtime.getRuntime();
		String fileName = null;
		String database = "ReverseEngineering";
		String[] collectionName = null;
		File dir = new File("F:\\Datasets");
		try {
			File[] listOfFiles = dir.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					System.out.println("File " + listOfFiles[i].getName());
					fileName = listOfFiles[i].getName();
				}
			}
			collectionName= fileName.split("\\.");
			System.out.println("Executing shell command to import file data into MongoDB");

			r.exec("c:\\windows\\system32\\cmd.exe /c mongoimport -d "+database+" -c " + collectionName[0]
					+ "_1 --type csv --file " + fileName +" --headerline", null, dir);
			
			
			@SuppressWarnings("resource")
			MongoDatabase mydatabase = new MongoClient().getDatabase("ReverseEngineering");
			FindIterable<Document> mydatabaserecords = mydatabase.getCollection(collectionName[0]).find();
			MongoCursor<Document> iterator = mydatabaserecords.iterator();
			
			if (iterator.hasNext()) {
				System.out.println("Data import has failed!!!!!!!!!!!!");
				System.out.println("seems like collection with same dataset name already exist");
				throw new DataWreakernterfaceException(com.data.datawreakerinterface.exception.ErrorCodes.SOMETHING_WENT_WRONG);
			} 
		}
			catch (IOException e) {
				e.printStackTrace();
			}
		dataSet.setResult("Data imported from file to MongoDB Successfully");
		dataSet.setCollectionName(collectionName[0]+"_1");
		return dataSet;
	}

	@Override
	public DatasetDetails exportDataAsCSV(String collectionName) throws DataWreakernterfaceException {
	/*	dataSet = new DatasetDetails();
		Runtime r = Runtime.getRuntime();
		File dir = new File("F:\\DatasetOutput");
		// String command = "mongoexport --db ReverseEngineering --collection NationalNames_1 --type=csv --fields Name,Year,Gender,Count,Boolean,Date --out ./output1.csv";
		String columnNames = getColumnNames(collectionName);
		try {
			r.exec("c:\\windows\\system32\\cmd.exe /c mongoexport --db ReverseEngineering --collection "+collectionName + "--type=csv --fields "+columnNames+" --out ./output5.csv" , null, dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("columnNames "+ getColumnNames(collectionName));
		dataSet.setCollectionName(collectionName);
		dataSet.setResult("Success");
		return dataSet;
		*/
		String db = "ReverseEngineering";
        String col = collectionName;
        String Host = "localhost";
        String Port = "27017";
        String fileName = "F:/DatasetOutput/outputDataset.csv";
        String columnNames = getColumnNames(collectionName);

        String command = "C:\\Program Files\\MongoDB\\Server\\4.0\\bin\\mongoexport.exe --host " + Host + " --port " + Port + " --db " + db + " --collection " + col + " --type=csv --fields "+columnNames+", --out " + fileName + "";

        try {
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            int waitFor = process.waitFor();
            System.out.println("waitFor:: " + waitFor);
            BufferedReader success = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String s = "";
            while ((s = success.readLine()) != null) {
                System.out.println("Success "+s);
            }

            while ((s = error.readLine()) != null) {
                System.out.println("Std ERROR : " + s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return null;
		
	}
	
	
	private String getColumnNames(String collectionName) {
		
		String columnNames = "";
		
		DataProfilerInfo dataprofilerInfo = getDataprofileInfo(collectionName);
		for(int i =0; i < dataprofilerInfo.getDatasetStats().size(); i++ ) {
			if(i==0) {
				columnNames = columnNames + dataprofilerInfo.getDatasetStats().get(i).getColumnName();	
			}else {
				columnNames = columnNames +","+ dataprofilerInfo.getDatasetStats().get(i).getColumnName();
			}
		}
		
		return columnNames;
	}

	
	private DataProfilerInfo getDataprofileInfo(String collectionName) {
		List<DataProfilerInfo> dataprofileInfoList = new ArrayList<DataProfilerInfo>();
		dataprofileInfoList = dataProfilerInfoRepo.findAll();
		DataProfilerInfo dataProfilerInfo = null;
		for(int i =0; i < dataprofileInfoList.size(); i++) {
			dataProfilerInfo = new DataProfilerInfo();
			if(dataprofileInfoList.get(i).getFileName().equals(collectionName)) {
				dataProfilerInfo = new DataProfilerInfo();
				dataProfilerInfo = dataprofileInfoList.get(i);
				break;
			}
	}
		return dataProfilerInfo;
}
	
	
}