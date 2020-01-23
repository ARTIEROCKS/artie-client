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
	
	
	/**
	 * Function to test the equality of two objects
	 */
	public boolean equals(Object obj) {
		
		//If the object is null or the object is not an instance of Sensor
		if (obj == null) return false;
	    if (!(obj instanceof Sensor)) return false;
	    
	    //If the object is this
	    if (obj == this) return true;
	    
	    //Casting the object
	    Sensor o = (Sensor) obj;
	    
	    //Checks all the attributes of the sensor
	    return (o.getSensorFile().equals(this.sensorFile) &&
	    		o.getSensorName().equals(this.sensorName) &&
	    		o.getSensorPort().equals(this.sensorPort) &&
	    		o.getManagementPort().equals(this.managementPort));
		
	}
	
	
}
