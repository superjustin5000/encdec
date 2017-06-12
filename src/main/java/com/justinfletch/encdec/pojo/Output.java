package com.justinfletch.encdec.pojo;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Output {

	@Expose
	@SerializedName("status_code")
	private Integer code = 500;
	
	
	@Expose
	@SerializedName("status_message")
	private String message = "";
	
	@Expose
	@SerializedName("data")
	private String data = "";

	
	public Output(Integer code, String message, String data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	
	
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	
	public String toString() {
		Gson gson = new Gson();
		String json = gson.toJson(this);
		return json;
	}
	
	
}
