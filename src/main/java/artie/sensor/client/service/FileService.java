package artie.sensor.client.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import artie.sensor.client.model.Sensor;

@Service
public class FileService {
	
	/**
	 * Function to write an object into a json file
	 * @param filePath
	 * @param objectToWrite
	 * @throws IOException
	 */
	public void writeJsonFile(String filePath, Object objectToWrite) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(filePath);
		mapper.writeValue(file, objectToWrite);
	}
	
	/**
	 * Read a json file and transform into a sensor array
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public Sensor[] readSensorJsonFile(String filePath) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(filePath);
		Sensor[] objectRead = mapper.readValue(file, Sensor[].class);
		return objectRead;
		
	}
	
}
