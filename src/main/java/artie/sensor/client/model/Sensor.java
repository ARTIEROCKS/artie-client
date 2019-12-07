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
	private String sensorName;
	@Column
	private String sensorFile;
	@Column
	private String sensorClass;
	
	/**
	 * Default constructor
	 */
	public Sensor(){}
	
	public Sensor(Long id, String sensorName, String sensorFile, String sensorClass){
		this.id=id;
		this.sensorName=sensorName;
		this.sensorFile=sensorFile;
		this.sensorClass=sensorClass;
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
	
	public String getSensorClass() {
		return sensorClass;
	}
	public void setSensorClass(String sensorClass) {
		this.sensorClass = sensorClass;
	}
	
}
