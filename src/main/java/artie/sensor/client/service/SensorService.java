package artie.sensor.client.service;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import artie.sensor.client.model.Sensor;
import artie.sensor.client.repository.SensorRepository;

@Service
public class SensorService {
	
	@Autowired
	private SensorRepository sensorRepository;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Object> sensorsLoaded = new ArrayList<Object>();
	
	/**
	 * Add a new sensor to the client
	 * @param pathToJar
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public void add(String pathToJar) throws IOException, ClassNotFoundException, 
											 InstantiationException, IllegalAccessException, 
											 NoSuchMethodException, SecurityException{
		
		URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);
		
		String[] pathElements = pathToJar.split("/");
		String pathElement = pathElements[pathElements.length-1];
		pathElements = pathElement.split("-");
		
		String propertiesFileName=pathElements[0];
		Enumeration<URL> listUrls = cl.getResources(propertiesFileName + ".properties");
		
		while(listUrls.hasMoreElements()){
			URL url = listUrls.nextElement();
			Properties prop = new Properties();
			prop.load(url.openStream());
			
			//Getting the sensor class property
			String sensorClass = prop.getProperty("artie.sensor.class");
			
			//If the sensor class is not null, we add it in the database
			if (sensorClass != null){
				Sensor sensor = new Sensor(0, propertiesFileName, pathToJar, sensorClass);
				sensorRepository.insert(sensor);
			}
			
		}
	}
	
	/**
	 * Function to run each sensor added
	 */
	public void run(){
		
		//1- Gets all the sensors from the database
		List<Sensor> sensorList = sensorRepository.findAll();
		
		//2- Loading the class dynamically
		for(Sensor sensor : sensorList){
			try {
				URL[] urls = { new URL("jar:file:" + sensor.getSensorFile()+"!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls);
				Class classToLoad = Class.forName(sensor.getSensorClass(), true, cl);
				Object instance = classToLoad.newInstance();
				sensorsLoaded.add(instance);
			} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage());
			}			
		}		
	}

}
