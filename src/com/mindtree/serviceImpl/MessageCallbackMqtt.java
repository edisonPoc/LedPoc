package com.mindtree.serviceImpl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.microsoft.azure.iothub.IotHubMessageResult;
import com.microsoft.azure.iothub.Message;
import com.mindtree.entity.Counter;
import com.mindtree.entity.DeviceDataCounter;
import com.mindtree.handler.DeviceClientSingleton;

public class MessageCallbackMqtt implements com.microsoft.azure.iothub.MessageCallback {
	String deviceID = "";

	@Override
	public IotHubMessageResult execute(Message msg, Object context) {
		Counter counter = (Counter) context;
		String recievedMessage = new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);
		JSONParser parser = new JSONParser();
		String ledState = "";
		JSONObject object = null;
		try {
		object = (JSONObject) parser.parse(recievedMessage);

			object = (JSONObject) object.get("Parameters");
			ledState = String.valueOf(object.get("LEDState"));
			System.out.println("the command recieved asks for state " + ledState);
			System.out.println("Received message " + counter.toString() + " with content: " + recievedMessage);
			System.out.println("Responding to message " + counter.toString() + " with " + IotHubMessageResult.COMPLETE);
			DeviceDataCounter deviceData = new DeviceDataCounter();
			System.out.println("adding data to list for index" + counter.get() + " for device ID" + deviceID);
			deviceData.setCounter(counter.get());
			deviceData.setDeviceId(deviceID);

			deviceData.setStatus(ledState);
			DeviceClientSingleton.addDeviceData(deviceData);
			System.out.println("added data with value " + DeviceClientSingleton.getDeviceData(deviceID));
			counter.increment();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return IotHubMessageResult.COMPLETE;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
}