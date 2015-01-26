package com.cloudshare.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.cloudshare.api.DTOs.ApiResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CSAPILowLevel {

	public static final String DEFAULT_URL_BASE = "https://use.cloudshare.com";
	public static final String DEFAULT_API_VERSION = "v2";

	public static final Comparator<String> caseInsensitiveComparator = new Comparator<String>() {
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	};

	private String mUrlBase;
	private String mApiVersion;
	private String mApiId;
	private String mApiKey;

	public CSAPILowLevel(String id, String key) {
		this(id, key, null, null);
	}

	public CSAPILowLevel(String id, String key, String apiVersion) {
		this(id, key, apiVersion, null);
	}

	public CSAPILowLevel(String id, String key, String apiVersion,
			String urlBase) {
		mApiId = id;
		mApiKey = key;
		mUrlBase = urlBase != null ? urlBase : DEFAULT_URL_BASE;
		mApiVersion = apiVersion != null ? apiVersion : DEFAULT_API_VERSION;
	}

	public ApiResponse callCSAPI(String commandCategory, String commandName)
			throws IOException, ApiException {
		return callCSAPI(commandCategory, commandName, null);
	}

	public ApiResponse callCSAPI(String commandCategory, String commandName,
			Map<String, String> params) throws IOException, ApiException {
		String url = generateApiUrl(commandCategory, commandName, params);
		return callURL(url);
	}

	public boolean CheckKeys() throws IOException, ApiException {
		callCSAPI("ApiTest", "Ping");
		return true;
	}

	private String generateApiUrl(String commandCategory, String commandName,
			Map<String, String> params) {
		Map<String, String> paramsMap = cloneParams(params);

		// calculate timestamp
		long epoch = System.currentTimeMillis() / 1000;
		String timeStamp = String.format("%d", epoch);

		// add mandatory keys
		paramsMap.put("timestamp", timeStamp);
		paramsMap.put("UserApiId", mApiId);
		if (mApiVersion != "v1") {
			paramsMap.put("token", GenerateRandomToken());
		}

		// sort the additional REST parameters
		List<String> ParamNameList = new ArrayList<String>(paramsMap.keySet());

		java.util.Collections.sort(ParamNameList, caseInsensitiveComparator);

		// build URL and calculate hash
		String query = "";
		String stringToSha = mApiKey
				+ (mApiVersion == "v1" ? "" : commandName.toLowerCase());
		try {
			for (String ParamName : ParamNameList) {
				if (ParamName.toLowerCase().equals("hmac"))
					continue;

				if (!query.isEmpty())
					query += '&';

				String value = paramsMap.get(ParamName);
				stringToSha += ParamName.toLowerCase() + value;

				query += ParamName + '=' + URLEncoder.encode(value, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

		return String.format("%s/Api/%s/%s/%s?%s&HMAC=%s", mUrlBase,
				mApiVersion, commandCategory, commandName, query,
				SHAsum(stringToSha));
	}

	public static ApiResponse callURL(String urlAddress) throws IOException,
			ApiException {
		String response = "";
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		URL url = new URL(urlAddress);

		try {

			try {
				connection = (HttpURLConnection) url.openConnection();
				reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
			} catch (IOException e) {
				if (connection != null && connection.getResponseCode() >= 0) {
					reader = new BufferedReader(new InputStreamReader(
							connection.getErrorStream()));
				} else {
					e.printStackTrace();
					throw e;
				}
			}

			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				response += inputLine;
			}

			int httpStatusCode = connection.getResponseCode();
			ApiResponse apiResponse = createApiResponseFromHttpResponse(
					response, httpStatusCode);

			if (httpStatusCode != 200) {
				throw new ApiException(apiResponse);
			} else {
				return apiResponse;
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private static ApiResponse createApiResponseFromHttpResponse(
			String content, int statusCode) throws IOException, ApiException {
		ApiResponse apiResponse;

		try {
			ObjectMapper mapper = new ObjectMapper();
			apiResponse = mapper.readValue(content, ApiResponse.class);
		} catch (JsonParseException | JsonMappingException e) {
			throw new ApiException(String.format(
					"Failed to deserialize API response:\n%s", content),
					statusCode);
		}

		if (apiResponse != null && apiResponse.status_code != null) {
			return apiResponse;
		}

		throw new ApiException(String.format("Got bad API response:\n%s",
				content), statusCode);
	}

	private static Map<String, String> cloneParams(Map<String, String> params) {
		Map<String, String> paramsCopy = new HashMap<String, String>();

		if (params == null) {
			return paramsCopy;
		}

		for (Entry<String, String> entry : params.entrySet()) {
			paramsCopy.put(entry.getKey(), entry.getValue());
		}

		return paramsCopy;
	}

	private static String GenerateRandomToken() {
		char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
				.toCharArray();
		StringBuilder token = new StringBuilder();

		Random rnd = new Random();
		for (int i = 0; i < 10; i++) {
			char c = characters[rnd.nextInt(characters.length)];
			token.append(c);
		}

		return token.toString();
	}

	private static String SHAsum(String convertme) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return bytesToHex(md.digest(convertme.getBytes("UTF-8")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String bytesToHex(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int v;

		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static class ApiException extends Exception {
		private static final long serialVersionUID = 1L;

		private ApiResponse mApiResponse;
		private int mHttpStatusCode;

		public ApiException(ApiResponse apiResponse) {
			super(String
					.format("ApiException (HTTP %d):\n%s: %s %s",
							parseHttpStatusCode(apiResponse),
							apiResponse.status_code,
							apiResponse.status_text,
							apiResponse.status_additional_data == null
									|| apiResponse.status_additional_data == null ? "" : " - "
									+ apiResponse.status_additional_data.toString()));
			mApiResponse = apiResponse;
			mHttpStatusCode = parseHttpStatusCode(apiResponse);
		}

		public ApiException(String message, int statusCode) {
			super(String.format("ApiException (HTTP %d):\n%s", statusCode,
					message));

			mApiResponse = null;
			mHttpStatusCode = statusCode;
		}

		public ApiResponse getApiResponse() {
			return mApiResponse;
		}

		public int getHttpStatusCode() {
			return mHttpStatusCode;
		}

		private static int parseHttpStatusCode(ApiResponse apiResponse) {
			if (apiResponse == null || apiResponse.status_code == null)
				return 500;

			try {
				return Integer
						.parseInt(apiResponse.status_code.substring(2, 3));
			} catch (NumberFormatException e) {
				return -1;
			}
		}
	}
}
