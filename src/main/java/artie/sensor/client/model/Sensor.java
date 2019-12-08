package artie.sensor.client.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sensor {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column
	private String sensorFile;
	@Column
	private Long sensorPort;
	@Column
	private Long managementPort;
	@Column
	private String sensorName;
	
	/**
	 * Default constructor
	 */
	public Sensor(){}
	
	public Sensor(Long id, String sensorFile, Long sensorPort, Long managementPort, String sensorName){
		this.id=id;
		this.sensorName=sensorName;
		this.sensorFile=sensorFile;
		this.sensorPort=sensorPort;
		this.managementPort=managementPort;
	}

	
	public long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getSensorName() {
		return sensorName;
	}
	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorFile() {
		return sensorFile;
	}
	public void setSensorFile(String sensorFile) {
		this.sensorFile = sensorFile;
	}
	
	public Long getSensorPort() {
		return sensorPort;
	}
	public void setSensorPort(Long sensorPort) {
		this.sensorPort = sensorPort;
	}
	
	public Long getManagementPort(){
		return managementPort;
	}
	public void setManagementPort(Long managementPort){
		this.managementPort = managementPort;
	}
	
}
