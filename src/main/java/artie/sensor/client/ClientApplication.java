package artie.sensor.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import artie.sensor.client.model.Sensor;
import artie.sensor.client.repository.SensorRepository;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Object> sensorsLoaded = new ArrayList<Object>();
	
	@Autowired
	private SensorRepository sensorRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
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
			} catch (MalformedURLException | ClassNotFoundException e) {
				logger.error(e.getMessage());
			}
			
		}		
	}

}
