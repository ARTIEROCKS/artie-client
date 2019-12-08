package artie.sensor.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import artie.sensor.client.enums.ActionEnum;
import artie.sensor.client.service.SensorService;

@SpringBootApplication
@EnableScheduling
public class ClientApplication implements CommandLineRunner {

	
	@Autowired
	private SensorService sensorService;
	
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	
		//If there are arguments
		if(args.length > 0){
			
			//If the action is RUN
			if(args[0].equalsIgnoreCase(ActionEnum.RUN.toString())){
				this.sensorService.run();
			}
			//If the action is ADD
			else if(args[0].equalsIgnoreCase(ActionEnum.ADD.toString())){
				if(args.length > 1){
					//Adds the sensor to the system
					this.sensorService.add(args[1]);
				}else{
					System.out.println("ERROR : 1 jar file path is needed after the action to be added");
				}
			}
		}else{
			//If there are no arguments
			System.out.println("ERROR: At least 1 action is needed");
		}
		
	}

}
