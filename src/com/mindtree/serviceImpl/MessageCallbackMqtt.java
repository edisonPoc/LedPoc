package com.mindtree.serviceImpl;

import com.microsoft.azure.iothub.IotHubMessageResult;
import com.microsoft.azure.iothub.Message;
import com.mindtree.entity.Counter;
import com.mindtree.entity.DeviceDataCounter;
import com.mindtree.handler.DeviceClientSingleton;

public class MessageCallbackMqtt implements com.microsoft.azure.iothub.MessageCallback
    {
	String deviceID="";
		@Override
        public IotHubMessageResult execute(Message msg, Object context)
        {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            System.out.println("Responding to message " + counter.toString() + " with " + IotHubMessageResult.COMPLETE);
            DeviceDataCounter deviceData=new DeviceDataCounter();
            System.out.println("adding data to list for index"+counter.get()+" for device ID"+ deviceID);
            deviceData.setCounter(counter.get());
            deviceData.setDeviceId(deviceID);
            
            deviceData.setStatus(new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            DeviceClientSingleton.addDeviceData(deviceData);
            System.out.println("added data with value "+DeviceClientSingleton.getDeviceData(deviceID));
            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }

        public String getDeviceID() {
			return deviceID;
		}
        public void setDeviceID(String deviceID) {
			this.deviceID = deviceID;
		}
    }