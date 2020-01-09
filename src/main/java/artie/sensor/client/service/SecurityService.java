package artie.sensor.client.service;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
	
	private final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	private final String password = "ArtiEClienT123@";
	
	/**
	 * Function to encrypt an element
	 * @param elementToEncrypt
	 * @return
	 */
	public String encrypt(String elementToEncrypt) {
		this.encryptor.setPassword(password);
		return this.encryptor.encrypt(elementToEncrypt);
		
	}
	
	/**
	 * Function to decrypt an element
	 * @param elementToDecrypt
	 * @return
	 */
	public String decrypt(String elementToDecrypt) {
		this.encryptor.setPassword(password);
		return this.encryptor.decrypt(elementToDecrypt);
	}

}
