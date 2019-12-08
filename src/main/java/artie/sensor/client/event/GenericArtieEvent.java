package artie.sensor.client.event;

import org.springframework.context.ApplicationEvent;

public class GenericArtieEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = 1L;
	private String message;
	protected boolean success;
	
	public GenericArtieEvent(Object source, String message, boolean success) {
		super(source);
		this.message = message;
		this.success = success;		
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
	
	
	
}
