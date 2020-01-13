package artie.sensor.client.service;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
	
	private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	
	@Value("${jasypt.encryptor.password}")
	private String password;
	
	/**
	 * Function to encrypt an element
	 * @param elementToEncrypt
	 * @return
	 */
	public String encrypt(String elementToEncrypt) {
		
		if(!this.encryptor.isInitialized()) {
			this.encryptor.setPassword(password);
		}
		return this.encryptor.encrypt(elementToEncrypt);
		
	}
	
	/**
	 * Function to decrypt an element
	 * @param elementToDecrypt
	 * @return
	 */
	public String decrypt(String elementToDecrypt) {
		
		if(!this.encryptor.isInitialized()) {
			this.encryptor.setPassword(password);
		}
		return this.encryptor.decrypt(elementToDecrypt);
	}

}
