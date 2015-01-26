package com.cloudshare.apitests;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Credentials {

	private String mApiId;
	private String mApiKey;

	/**
	 Constructor - get credentials parameters
	 */
	public Credentials(String apiId, String apiKey){
		mApiId = apiId;
		mApiKey = apiKey;
	}

	/**
	 * Constructor - get credentials from file 
	 * @throws CredentialsException 
	 */
	public Credentials(String fileName) {
		String apiId = "";
		String apiKey = "";
		
		FileInputStream fileStream = null;
		DataInputStream inputStream = null;
		try {
			fileStream = new FileInputStream(fileName);
			inputStream = new DataInputStream(fileStream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String strLine;
			strLine = reader.readLine();
			if (strLine == null){
				throw new CredentialsException("Problem when trying to read credentials from " + fileName);
			}

			apiId = strLine.split("=")[1];

			strLine = reader.readLine();
			if (strLine == null) {
				throw new CredentialsException("Problem when trying to read credentials from " + fileName);
			}
			
			apiKey = strLine.split("=")[1];
			
		} catch (FileNotFoundException e) {
			throw new CredentialsException("Could not find credentials file: " + fileName);
		} catch (IOException | ArrayIndexOutOfBoundsException e) {
			throw new CredentialsException("Problem reading data from file: " + fileName);
		} finally {
			try {
				if (fileStream != null) {
					fileStream.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
			}
		}

		mApiId = apiId;
		mApiKey = apiKey;
	}
	
	public String getId() {
		return mApiId;
	}
	
	public String getKey() {
		return mApiKey;
	}
}

class CredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CredentialsException() {
	}
	
	public CredentialsException(String message) {
		super(message);
	}
}
