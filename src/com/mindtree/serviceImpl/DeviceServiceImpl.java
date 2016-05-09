package com.mindtree.serviceImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.mindtree.dao.DeviceDaoImpl;
import com.mindtree.entity.DeviceData;
import com.mindtree.entity.DeviceDataCounter;
import com.mindtree.handler.DeviceClientSingleton;

public class DeviceServiceImpl {
	DeviceDaoImpl deviceDao=null;
	private static String connectionString = "HostName=LedIotSuite.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=0mpAS5WMeuJJ2g6jGyvdWBidmIzPONV5ovT3HOfoUA8=";
	//private static IotHubClientProtocol protocol = IotHubClientProtocol.AMQPS;

	public void sendDeviceData(String data,String deviceId) throws URISyntaxException, IOException, InterruptedException {
		//DeviceClient client = DeviceClientSingleton.getInstance();
		//client.open();
		DeviceData telemetryDataPoint = new DeviceData();
		telemetryDataPoint.deviceId = deviceId;
		telemetryDataPoint.status = data;
		
		String msgStr = telemetryDataPoint.serialize();
		Message msg = new Message(msgStr);
		System.out.println(msgStr);
		Object lockobj = new Object();
		EventCallback callback = new EventCallback();
		List<Device> devices=DeviceClientSingleton.getDeviceList();
		Iterator<Device> iter=devices.iterator();
		int index=0;
		while(iter.hasNext()) {
			
	         Device element = iter.next();
	         if(element.getDeviceId().equals(deviceId))
	         {
	        	 break;
	         }
	         ++index;
	      }
		DeviceClientSingleton.getInstance(index).sendEventAsync(msg, callback, lockobj);
		synchronized (lockobj) {
			lockobj.wait();
		}
		Thread.sleep(1000);
		//client.close();
	}
	public List<String> getAllDevices() throws Exception
	{
		List<String> allDevices=new ArrayList<String>();
		List<Device> devices=DeviceClientSingleton.getDeviceList();
		System.out.println("size "+devices.size());
		for(Device dev:devices)
		{
			System.out.println(dev.getCloudToDeviceMessageCount());
			System.out.println(dev.getDeviceId());
			System.out.println(dev.getLastActivityTime());
			System.out.println(dev.getConnectionState());
			System.out.println(dev.getStatus());
			allDevices.add(dev.getDeviceId());
		}
		return allDevices;
	}
	public List<String> getCommandStatus() {
		// TODO Auto-generated method stub 
		List<Device> devices=DeviceClientSingleton.getDeviceList();
		List<String> commandStatus=new ArrayList<String>();
		for(int i=0;i<devices.size();i++)
		{
			String deviceData=DeviceClientSingleton.getDeviceData(devices.get(i).getDeviceId());
			commandStatus.add(deviceData);
			if((!deviceData.equals(""))&&(!deviceData.equals(" ")))
			{
				DeviceClientSingleton.removeDeviceData(devices.get(i).getDeviceId());
			}
			
			 
		}
		return commandStatus;
	}

}