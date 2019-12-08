package artie.sensor.client.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import artie.sensor.client.event.GenericArtieEvent;
import artie.sensor.client.model.Sensor;
import artie.sensor.client.repository.SensorRepository;

@Service
public class SensorService {
	
	@Autowired
	private SensorRepository sensorRepository;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${artie.client.minsensorport}")
	private Long minSensorPort;
	
	/**
	 * Function to get the next free port in the client system
	 * @return
	 */
	private Long getNextPort(){
		
		Long nextPort = minSensorPort;
		
		//1- Getting all the sensors ordered by sensor number
		Optional<Sensor> sensorList = sensorRepository.findByOrderBySensorPortDesc();
		
		//2- If there is a sensor added in the system, we set the new sensor port as the lastest one + 10
		if(sensorList.isPresent()){
			nextPort = sensorList.get().getSensorPort();
			nextPort += 10;
		}
		
		return nextPort;
	}
	
	/**
	 * Add a new sensor to the client
	 * @param pathToJar
	 */
	public void add(String pathToJar){
		
		//1- Getting the sensor name from the jar file name
		String[] pathElements = pathToJar.split("/");
		String pathElement = pathElements[pathElements.length-1];
		pathElements = pathElement.split("-");
		String sensorName=pathElements[0];
		
		//2- Getting the sensor port and the management port
		Long sensorPort = this.getNextPort();
		Long managementPort = sensorPort + 1;
		
		//3- Inserting the sensor in the system
		this.sensorRepository.save(new Sensor((long) 0, pathToJar, sensorPort, managementPort, sensorName));
		
		//4- Triggering the event
		this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, "Sensor - Add - " + sensorName, true));

		//5- Logging the action
		this.logger.debug("Sensor - Add - " + sensorName);
	}
	
	/**
	 * Function to run each sensor added
	 */
	public void run(){
		
		//1- Gets all the sensors from the database
		List<Sensor> sensorList = (List<Sensor>) sensorRepository.findAll();
		
		//2- Loading the class dynamically
		for(Sensor sensor : sensorList){
			
		}		
	}
}
