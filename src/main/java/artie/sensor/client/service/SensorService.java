package artie.sensor.client.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import artie.sensor.client.enums.SensorActionEnum;
import artie.sensor.client.event.GenericArtieEvent;
import artie.sensor.client.model.Sensor;
import artie.sensor.common.dto.SensorObject;
import artie.sensor.common.enums.ConfigurationEnum;
import artie.sensor.common.model.SensorData;
import artie.sensor.common.repository.SensorDataRepository;

@Service
public class SensorService {
	
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Value("${artie.client.minsensorport}")
	private Long minSensorPort;
	
	@Value("${artie.client.waitsensorstart}")
	private Long waitSensorStart;
	
	@Value("${artie.client.sensorretries}")
	private Long sensorRetries;
	
	@Value("${artie.client.datasource.url}")
	private String sensorDataSourceUrl;
	
	@Value("${spring.datasource.driverClassName}")
	private String dataSourceDriver;
	
	@Value("${spring.datasource.username}")
	private String dataSourceUser;
	
	@Value("${spring.datasource.password}")
	private String dataSourcePasswd;
	
	@Value("${artie.client.sensorfile.path}")
	private String sensorFilePath;
	
	@Value("${logging.level.root}")
	private String logginLevelRoot;
	
	@Value("${logging.level.artie.sensor}")
	private String loggingLevelArtieSensor;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private SensorDataRepository sensorDataRepository;
	
	private Logger logger = LoggerFactory.getLogger(SensorService.class);
	private List<Sensor> sensorList = new ArrayList<Sensor>();
	private Map<String, Boolean> runningSensors = new HashMap<>();
	private boolean loadingProcessFinished = false;
	private RestTemplate restTemplate = new RestTemplate();
	private Map<String, Map<String,String>> sensorsConfiguration = new HashMap<>();
	
	
	@PostConstruct
	public void init(){
		
		File file = new File(this.sensorFilePath);
		if(file.exists()) {
			//1- Gets all the sensors from the json file
			try {
				Sensor[] sensors = fileService.readSensorJsonFile(this.sensorFilePath);
				this.sensorList = new ArrayList<>(Arrays.asList(sensors));
				this.startUpSensorServices();
			} catch (IOException | InterruptedException e) {
				this.logger.error(e.getMessage());
			}
			
		}
	}
	
	
	/**
	 * Function to destroy all the living sensor processes
	 */
	@PreDestroy
	public void destroy(){
		
		//Stops all the sensors
		this.stopSensors();	
	}
	
	
	/**
	 * Method to start the sensor services
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void startUpSensorServices() throws IOException, InterruptedException {
		
		for(Sensor sensor : this.sensorList){
			
			//1- Checks if the service is already alive or not
			boolean isAlive = false;
			boolean isStarted = false;
			isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
			
			//2- If the sensor is not alive, we run the process
			if(!isAlive) {
				
				Runtime.getRuntime().exec("java -jar " + sensor.getSensorFile() + 
											" --server.port=" + sensor.getSensorPort().toString() + 
											" --management.server.port=" + sensor.getManagementPort().toString() + 
											" --logging.level.org.springframework=" + this.logginLevelRoot + 
											" --logging.level.artie.sensor=" + this.loggingLevelArtieSensor);
				
				//Waiting to the sensor be alive
				int retryNumber = 0;
				while(!isAlive && retryNumber < this.sensorRetries) {
					
					Thread.sleep(this.waitSensorStart);
					isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
					retryNumber++;
				}
			}
		}
		
	}
	
	
	/**
	 * Get all the sensors
	 */
	public List<Sensor> getSensors() {
		return this.sensorList;	
	}
	
	/**
	 * Function to get the configuration set in all the sensors
	 * @return
	 */
	public Map<String, Map<String,String>> getAllSensorsConfiguration(){
		return this.sensorsConfiguration;
	}
	
	
	/**
	 * Function to shutdown all the sensors
	 */
	public void stopSensors(){
		
		//Stopping all the sensors
		for(Sensor sensor : sensorList){
			
			//If the sensor is running
			if(this.runningSensors.get(sensor.getSensorName())) {
				
				//1- Stopping the service
				this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/stop", String.class);
				boolean isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
				
				//Waiting to the sensor be down
				int retryNumber = 0;
				while(isAlive && retryNumber < this.sensorRetries) {
					
					try {
						Thread.sleep(this.waitSensorStart);
					} catch (InterruptedException e) {
						this.logger.error(e.getMessage());
					}
					
					isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
					retryNumber++;
				}
				
				//Triggering and logging the Stop action
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.STOP.toString(), sensor.getSensorName(), true));
				this.logger.debug("Sensor - " + SensorActionEnum.STOP.toString() + " - " + sensor.getSensorName() + " - OK");
				
				//2- Stopping the springboot
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<String> entity = new HttpEntity<String>("", headers);
				this.restTemplate.postForEntity("http://localhost:" + sensor.getManagementPort() + "/actuator/shutdown", entity, String.class);
				
				//Triggering and logging the shutdown action
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.SHUTDOWN.toString(), sensor.getSensorName(), true));
				this.logger.debug("Sensor - " + SensorActionEnum.SHUTDOWN.toString() + " - " + sensor.getSensorName() + " - OK");
				
				this.runningSensors.replace(sensor.getSensorName(), false);
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
			
			//1- Gets all the sensor data from the database
			List<SensorData> sensorDataList = this.sensorDataRepository.findAll();
			
			if(sensorDataList.size() > 0) {
								
				//2-  Sends all the sensor daya by event, and deletes the sensor data obtained
				ObjectMapper mapper = new ObjectMapper();
				List<SensorObject> listSensorObject = new ArrayList<>();
				sensorDataList.forEach(sd -> {
					
					try {
						listSensorObject.add(mapper.readValue(sd.getData(),new TypeReference<SensorObject>(){}));						
					} catch (JsonProcessingException e) {
						this.logger.error(e.getMessage());
					}
					this.sensorDataRepository.delete(sd.getId());
				});
				
				this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.SEND.toString(), listSensorObject));
				
			}	
			
			//3- Requesting to send their data into the database
			for(Sensor sensor : sensorList){
				
				//If the sensor is running
				if(this.runningSensors.get(sensor.getSensorName())) {
					//Gets the sensor object
					this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/sendSensorData", String.class);
	
					//Logging the results
					this.logger.debug("Sensor - " + SensorActionEnum.SEND.toString() + " - " + sensor.getSensorName() + " - OK");
					System.out.println("Sensor - " + SensorActionEnum.SEND.toString() + " - " + sensor.getSensorName() + " - OK");
				}
			}
		}
	}	
	
	/**
	 * Function to get the next free port in the client system
	 * @return
	 */
	private Long getNextPort(){
		
		Long nextPort = minSensorPort;
		int sensorListSize = this.sensorList.size();
		
		if(sensorListSize > 0) {
			
			//1- Sorting the sensors by their sensor port
			Collections.sort(this.sensorList, Comparator.comparing(s -> s.getSensorPort()));
		
			//2- If there is a sensor added in the system, we set the new sensor port as the lastest one + 10
			nextPort = sensorList.get(sensorListSize - 1).getSensorPort();
			nextPort += 10;
		}
		
		return nextPort;
	}
	
	/**
	 * Add a new sensor to the client
	 * @param pathToJar
	 * @throws IOException 
	 */
	public void add(String pathToJar) throws IOException{
		
		//1- Getting the sensor name from the jar file name
		String[] pathElements = pathToJar.split("/");
		String pathElement = pathElements[pathElements.length-1];
		pathElements = pathElement.split("-");
		String sensorName=pathElements[0];
		
		//2- Getting the sensor port and the management port
		Long sensorPort = this.getNextPort();
		Long managementPort = sensorPort + 1;
		
		//3- Inserting the sensor in the file and in the list
		this.sensorList.add(new Sensor(pathToJar, sensorPort, managementPort, sensorName));
		this.fileService.writeJsonFile(this.sensorFilePath, this.sensorList);
		
		//4- Triggering the event
		this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.ADD.toString(), sensorName, true));

		//5- Logging the action
		this.logger.debug("Sensor - " + SensorActionEnum.ADD.toString() + " - " + sensorName + " - OK");
	}
	
	/**
	 * Function to run each sensor added in database
	 * @throws IOException 
	 */
	public void run() throws IOException{

		
		//1- Prepares the configuration
		Map<String,String> sensorConfiguration = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		
		//2- Running all the sensors with the parameters stored in database
		for(Sensor sensor : this.sensorList){
			
			try {
				
				//2.1- Checks if the service is already alive or not
				boolean isAlive = false;
				boolean isStarted = false;
				isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
				
				//2.1.1- If the sensor is not alive, we run the process
				if(!isAlive) {
					
					Runtime.getRuntime().exec("java -jar " + sensor.getSensorFile() + 
												" --server.port=" + sensor.getSensorPort().toString() + 
												" --management.server.port=" + sensor.getManagementPort().toString() + 
												" --logging.level.org.springframework=" + this.logginLevelRoot + 
												" --logging.level.artie.sensor=" + this.loggingLevelArtieSensor);
					
					//Waiting to the sensor be alive
					int retryNumber = 0;
					while(!isAlive && retryNumber < this.sensorRetries) {
						
						Thread.sleep(this.waitSensorStart);
						isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
						retryNumber++;
					}
				}
				
				//2.1.2- If the sensor is alive, we start the sensor 
				if(isAlive) {
					
					//Triggering the action
					this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.RUN.toString(), sensor.getSensorName(), true));
					
					//Logging the action
					this.logger.debug("Sensor - " + SensorActionEnum.RUN.toString() + " - " + sensor.getSensorName() + " - OK");
					
					if(isStarted) {
						//If the sensor is already started, we first stop it
						this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/stop", String.class);
					}
					
					//2.2- Getting the configuration from the sensor
					String jsonSensorConfiguration = this.restTemplate.getForObject("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/getConfiguration", String.class);
					
					//convert JSON string to Map
					sensorConfiguration = mapper.readValue(jsonSensorConfiguration, new TypeReference<HashMap<String,String>>(){});
	
					
					//2.2- Sets the parameters values in the sensor configuration
					sensorConfiguration.replace(ConfigurationEnum.DB_URL.toString(), this.sensorDataSourceUrl);
					sensorConfiguration.replace(ConfigurationEnum.DB_DRIVER_CLASS.toString(), this.dataSourceDriver);
					sensorConfiguration.replace(ConfigurationEnum.DB_USER.toString(), this.dataSourceUser);
					sensorConfiguration.replace(ConfigurationEnum.DB_PASSWD.toString(), this.dataSourcePasswd);					
					
					//2.3- Sets the new parameters in the sensor configuration
					jsonSensorConfiguration = mapper.writeValueAsString(sensorConfiguration);
					this.restTemplate.postForObject("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/configuration", jsonSensorConfiguration, String.class);
					
					//2.4- Adds the sensor configuration to the existing map
					this.sensorsConfiguration.put(sensor.getSensorName(), sensorConfiguration);
					
					//2.5- Starting the sensor
					this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/start", String.class);
					
					//Triggering the action
					this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.START.toString(), sensor.getSensorName(), true));
					
					//Logging the action
					this.logger.debug("Sensor - " + SensorActionEnum.START.toString() + " - " + sensor.getSensorName() + " - OK");
					
					//Setting the sensor in the running sensor map
					this.runningSensors.put(sensor.getSensorName(), true);
				}
				
			} catch (Exception e) {
				
				this.logger.error(e.getMessage());
			}
		}
		
		//Loading process finished if there are at least 1 sensor running
		this.loadingProcessFinished = this.runningSensors.containsValue(true);
	}
	
	
	/**
	 * Function to delete a sensor from the list a return the list again
	 * @param sensor
	 * @return
	 */
	public List<Sensor> deleteSensor(Sensor sensor) {
		
		//1- Checks if the sensor is running or not
		if(this.runningSensors != null && this.runningSensors.size() > 0 && this.runningSensors.get(sensor.getSensorName())) {
			
			//1.1- Stops the service
			this.restTemplate.getForEntity("http://localhost:" + sensor.getSensorPort() + "/artie/sensor/" + sensor.getSensorName() + "/stop", String.class);
			boolean isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
			
			//1.2- Waiting to the sensor be down
			int retryNumber = 0;
			while(isAlive && retryNumber < this.sensorRetries) {
				
				try {
					Thread.sleep(this.waitSensorStart);
				} catch (InterruptedException e) {
					this.logger.error(e.getMessage());
				}
				
				isAlive = this.sensorIsAlive(sensor.getSensorPort(), sensor.getSensorName());
				retryNumber++;
			}
		}
		
		//When the sensor is down, we delete from the list and we save again the list
		this.sensorList.remove(sensor);
		
		try {
			this.fileService.writeJsonFile(this.sensorFilePath, this.sensorList);
		} catch (IOException e) {
			this.logger.error(e.getMessage());
		}
		
		//2- Triggering the event
		this.applicationEventPublisher.publishEvent(new GenericArtieEvent(this, SensorActionEnum.DEL.toString(), sensor.getSensorName(), true));

		//3- Logging the action
		this.logger.debug("Sensor - " + SensorActionEnum.DEL.toString() + " - " + sensor.getSensorName() + " - OK");
		
		
		return this.sensorList;
	}
	
	/**
	 * Function to test if the sensor is alive or not
	 * @return
	 */
	private boolean sensorIsAlive(Long sensorPort, String sensorName) {
		
		boolean isAlive = false;
		
		try {
			ResponseEntity<Boolean> response = this.restTemplate.getForEntity("http://localhost:" + sensorPort + "/artie/sensor/" + sensorName + "/isAlive", boolean.class);
			isAlive = (response != null) ? response.getBody() : false; 
		} catch(Exception ex) {
			isAlive = false;
		}
		
		return isAlive;
	}
	
}
