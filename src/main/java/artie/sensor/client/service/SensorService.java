package artie.sensor.client.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
	
	public void add(String pathToJar) throws IOException{
		
		JarFile jarFile = new JarFile(pathToJar);
		URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
		URLClassLoader cl = URLClassLoader.newInstance(urls);
		URL url = cl.findResource("application.properties");
		Properties prop = new Properties();
		prop.load(url.openStream());
		
		//Getting the test property
		String rate = prop.getProperty("artie.sensor.keyboardmouse.rate");
		System.out.println(rate);
	}
	
	public void delete(){
		
	}
	
	public void run(){
		
		//1- Gets all the sensors from the database
		List<Sensor> sensorList = sensorRepository.findAll();
		
		//2- Loading the class dynamically
		for(Sensor sensor : sensorList){
			try {
				JarFile jarFile = new JarFile(sensor.getSensor_file());
				URL[] urls = { new URL("jar:file:" + sensor.getSensor_file()+"!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls);
				Class classToLoad = Class.forName(sensor.getSensor_class(), true, cl);
				Object instance = classToLoad.newInstance();
				sensorsLoaded.add(instance);
			} catch (ClassNotFoundException | IOException | InstantiationException | IllegalAccessException e) {
				logger.error(e.getMessage());
			}			
		}		
	}

}
