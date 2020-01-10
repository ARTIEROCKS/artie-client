package artie.sensor.client;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import artie.sensor.client.enums.ActionEnum;
import artie.sensor.client.service.SecurityService;
import artie.sensor.client.service.SensorService;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories("artie.sensor")
@EntityScan("artie.sensor")
@ComponentScan(basePackages = "artie.sensor")
public class ClientApplication implements CommandLineRunner {

	@Value("${artie.client.library}")
	private boolean isLibrary;
	
	@Autowired
	private SensorService sensorService;
	
	@Autowired
	private SecurityService securityService;
	
	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

	//@Override
	public void run(String... args) throws Exception {
	
		//If the client is not considered a library
		if(!isLibrary) {
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
						this.sensorService.run();
					}else{
						System.out.println("ERROR : 1 jar file path is needed after the action to be added");
					}
				}
				else if(args[0].equalsIgnoreCase(ActionEnum.ENC.toString())){
					if(args.length > 1) {
						System.out.println(this.securityService.encrypt(args[1]));
					}else {
						System.out.println("ERROR : 1 string to encrypt is needed");
					}					
				}
			}else{
				//If there are no arguments
				System.out.println("ERROR: At least 1 action is needed");
			}
		}
		
	}
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseaServer() throws SQLException {
	    return Server.createTcpServer(
	      "-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
	}
	
}
