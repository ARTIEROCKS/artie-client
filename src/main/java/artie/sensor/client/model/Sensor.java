package artie.sensor.client.model;

public class Sensor {

	private long id;
	private String sensor_name;
	private String sensor_file;
	private String sensor_class;
	
	/**
	 * Default constructor
	 */
	public Sensor(){}
	
	public Sensor(long id, String sensor_name, String sensor_file, String sensor_class){
		this.id=id;
		this.sensor_name=sensor_name;
		this.sensor_file=sensor_file;
		this.sensor_class=sensor_class;
	}

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getSensor_name() {
		return sensor_name;
	}
	public void setSensor_name(String sensor_name) {
		this.sensor_name = sensor_name;
	}

	public String getSensor_file() {
		return sensor_file;
	}
	public void setSensor_file(String sensor_file) {
		this.sensor_file = sensor_file;
	}
	
	public String getSensor_class() {
		return sensor_class;
	}
	public void setSensor_class(String sensor_class) {
		this.sensor_class = sensor_class;
	}
	
}
