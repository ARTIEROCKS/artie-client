package artie.sensor.client.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * Read a json file and transform into an object
	 * @param filePath
	 * @return
	 * @throws IOException 
	 */
	public Object readJsonFile(String filePath) throws IOException {
		
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(filePath);
		Object objectRead = mapper.readValue(file, Object.class);
		return objectRead;
		
	}
	
}
