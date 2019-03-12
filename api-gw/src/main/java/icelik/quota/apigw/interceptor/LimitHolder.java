package icelik.quota.apigw.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LimitHolder {

	private static ObjectMapper objectMapper = new ObjectMapper();

	private String key;
	private Long treshold;
	private Long blockDuration;

	public LimitHolder(String key, Long treshold, Long blockDuration) {
		this.key = key;
		this.treshold = treshold;
		this.blockDuration = blockDuration;
	}

	public String getKey() {
		return key;
	}

	public Long getTreshold() {
		return treshold;
	}

	public Long getBlockDuration() {
		return blockDuration;
	}

	public String toJsonString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
