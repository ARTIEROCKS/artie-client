package artie.sensor.client.model;

public class Sensor {

	private long id;
	private String sensorName;
	private String sensorFile;
	private String sensorClass;
	
	/**
	 * Default constructor
	 */
	public Sensor(){}
	
	public Sensor(long id, String sensorName, String sensorFile, String sensorClass){
		this.id=id;
		this.sensorName=sensorName;
		this.sensorFile=sensorFile;
		this.sensorClass=sensorClass;
	}

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
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
	
	public String getSensorClass() {
		return sensorClass;
	}
	public void setSensorClass(String sensorClass) {
		this.sensorClass = sensorClass;
	}
	
}
