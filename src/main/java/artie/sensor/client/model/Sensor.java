package artie.sensor.client.model;

public class Sensor {

	private String sensorFile;
	private Long sensorPort;
	private Long managementPort;
	private String sensorName;
	
	/**
	 * Default constructor
	 */
	public Sensor(){}
	
	public Sensor(String sensorFile, Long sensorPort, Long managementPort, String sensorName){
		this.sensorName=sensorName;
		this.sensorFile=sensorFile;
		this.sensorPort=sensorPort;
		this.managementPort=managementPort;
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
