package com.mindtree.entity;

import com.google.gson.Gson;

public class DeviceData {
	  public String deviceId;
	  public String status;

	  public String serialize() {
	    Gson gson = new Gson();
	    return gson.toJson(this);
	  }
	}