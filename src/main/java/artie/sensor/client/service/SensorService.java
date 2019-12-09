package artie.sensor.client.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import artie.sensor.client.enums.SensorActionEnum;
import artie.sensor.client.event.GenericArtieEvent;
import artie.sensor.client.model.Sensor;
import artie.sensor.client.repository.SensorRepository;
import artie.sensor.common.dto.SensorObject;

@Service
public class SensorService {
	
	@Autowired
	private SensorRepository sensorRepository;
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Value("${artie.client.minsensorport}")
	private Long minSensorPort;
	
	@Value("${artie.client.waitsensorstart}")
	private Long waitSensorStart;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private List<Process> sensorProcessList = new ArrayList<Process>();
	private List<Sensor> sensorList = new ArrayList<Sensor>();
	private boolean loadingProcessFinished = false;
	private RestTemplate restTemplate = new RestTemplate();
	
	
	/**
	 * Function to destroy all the living sensor processes
	 */
	@PreDestroy
	public void destroy(){
		
		//Stopping all the sensors
		for(Sensor sensor : sensorList){
			this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/stop", String.class);
			
			//Triggering the action
			this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.STOP.toString(), sensor.getSensorName(), true));
			
			//Logging the action
			this.logger.debug("Sensor - " + SensorActionEnum.STOP.toString() + " - " + sensor.getSensorName() + " - OK");
		}
		
		//All the processes will be destroyed
		for(Process sensorProcess : this.sensorProcessList){
			if(sensorProcess.isAlive()){
				sensorProcess.destroyForcibly();
			}
		}
	}
	
	/**
	 * Function to add the sensor data
	 */
	@Scheduled(fixedRateString="${artie.client.getdata.rate}")
	public void getSensorData(){
		
		//If the loading process has been finished, we get all the data from the sensors
		if(this.loadingProcessFinished){
			for(Sensor sensor : sensorList){
				
				//Gets the sensor object
				ResponseEntity<SensorObject[]> response = this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/getSensorData", SensorObject[].class);
				
				//Transforming the result from an array to a list
				SensorObject[] sensorObjects = response.getBody();
				List<SensorObject> sensorObjectList = new ArrayList<SensorObject>();
				
				for(int i=0; i<sensorObjects.length; i++){
					sensorObjectList.add(sensorObjects[i]);
				}
				
				//Triggering the results
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.READ.toString(), sensor.getSensorName(), sensorObjectList, true));

				//Logging the results
				this.logger.debug("Sensor - " + SensorActionEnum.READ.toString() + " - " + sensor.getSensorName() + " - OK");
				System.out.println("Sensor - " + SensorActionEnum.READ.toString() + " - " + sensor.getSensorName() + " - OK");
			}
		}
	}
	
	
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
		this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.ADD.toString(), sensorName, true));

		//5- Logging the action
		this.logger.debug("Sensor - " + SensorActionEnum.ADD.toString() + " - " + sensorName + " - OK");
	}
	
	/**
	 * Function to run each sensor added in database
	 */
	public void run(){
		
		//1- Gets all the sensors from the database
		List<Sensor> sensorList = (List<Sensor>) sensorRepository.findAll();
		
		//3- Running all the sensors with the parameters stored in database
		for(Sensor sensor : sensorList){
			try {
				
				//1- Running the sensor service
				Process sensorProcess = Runtime.getRuntime().exec("java -jar " + sensor.getSensorFile() + 
																	" --server.port=" + sensor.getSensorPort().toString() + 
																	" --management.server.port=" + sensor.getManagementPort().toString());
				this.sensorProcessList.add(sensorProcess);
				this.sensorList.add(sensor);
				Thread.sleep(this.waitSensorStart);
				
				//Triggering the action
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.RUN.toString(), sensor.getSensorName(), true));
				
				//Logging the action
				this.logger.debug("Sensor - " + SensorActionEnum.RUN.toString() + " - " + sensor.getSensorName() + " - OK");
				
				//2- Starting the sensor
				this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/start", String.class);
				
				//Triggering the action
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.START.toString(), sensor.getSensorName(), true));
				
				//Logging the action
				this.logger.debug("Sensor - " + SensorActionEnum.START.toString() + " - " + sensor.getSensorName() + " - OK");
				
			} catch (IOException | InterruptedException e) {
				this.logger.error(e.getMessage());
			}
		}
		
		//Loading process finished
		this.loadingProcessFinished = true;
	}
}
