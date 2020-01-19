package artie.sensor.client.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;
import artie.sensor.common.dto.SensorObject;


public class GenericArtieEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;
	protected String action;
	protected String message;
	protected String sensorName;
	protected boolean success;
	protected List<SensorObject> data;
	
	public GenericArtieEvent(Object source, String action, String sensorName, boolean success){
		super(source);
		this.action = action;
		this.sensorName = sensorName;
		this.success = success;
	}
	
	public GenericArtieEvent(Object source, String action, String sensorName, String message, boolean success) {
		super(source);
		this.message = message;
		this.action = action;
		this.sensorName = sensorName;
		this.success = success;		
	}
	
	public GenericArtieEvent(Object source, String action, String sensorName, List<SensorObject> data, boolean success){
		super(source);
		this.action = action;
		this.sensorName = sensorName;
		this.data = data;
		this.success = success;
	}
	
	public GenericArtieEvent(Object source, String action, List<SensorObject> data) {
		super(source);
		this.action = action;
		this.data = data;
	}

	public String getAction(){
		return this.action;
	}
	public void setAction(String action){
		this.action = action;
	}
	
	public String getSensorName(){
		return this.sensorName;
	}
	public void setSensorName(String sensorName){
		this.sensorName = sensorName;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public List<SensorObject> getData(){
		return this.data;
	}
	public void setData(List<SensorObject> data){
		this.data = data;
	}
	
}
