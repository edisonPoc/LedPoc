package com.mindtree.serviceImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONObject;

import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.mindtree.dao.DeviceDaoImpl;
import com.mindtree.entity.DeviceData;
import com.mindtree.entity.DeviceDataCounter;
import com.mindtree.entity.DeviceEntity;
import com.mindtree.handler.DeviceClientSingleton;

public class DeviceServiceImpl {
	DeviceDaoImpl deviceDao = null;
	private static String connectionString = "HostName=LedIotSolution.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=HWYsgV2YckGE4K5qBmWKmLZJbkMaIR5pYgId3b2H8N8=";
	// private static IotHubClientProtocol protocol =
	// IotHubClientProtocol.AMQPS;
	public static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=lediotsolution;"
			+ "AccountKey=4UmXKhpd+9VUL3usGRVj3hspk+oP85YIzxEiVwjWQNjzZLz7tfuNTAD+a3BuAReG0YLCJ7yjam/1Ywsw3TveXQ==";

	public void sendDeviceData(String data, String deviceId,boolean isGladiusChild)
			throws URISyntaxException, IOException, InterruptedException, StorageException, InvalidKeyException {
		// DeviceClient client = DeviceClientSingleton.getInstance();
		// client.open();
		DeviceData telemetryDataPoint = new DeviceData();
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the table client.
		CloudTableClient tableClient = storageAccount.createCloudTableClient();

		// Create a cloud table object for the table.
		CloudTable cloudTable = tableClient.getTableReference("DeviceList");
		
		TableOperation retrieveData = TableOperation.retrieve("LedIotSolution",deviceId, DeviceEntity.class);
		DeviceEntity specificEntity = cloudTable.execute(retrieveData).getResultAsType();
		System.out.println("Device sending the data is of Type : "+specificEntity.getType());
		if((specificEntity.getType().equals("Simulated"))||(specificEntity.getType().equals("Rasberry")))
		{
			telemetryDataPoint.n="";
			telemetryDataPoint.s="";
			telemetryDataPoint.deviceId=deviceId;
			JSONObject dataObject = new JSONObject();
			dataObject.put("status", data);
			telemetryDataPoint.deviceData = dataObject.toJSONString();
		}
		else if((specificEntity.getType().equals("Gladius_Parent"))&&(isGladiusChild==false))
		{
			telemetryDataPoint.n="data";
			telemetryDataPoint.s="123";
			JSONObject dataObject = new JSONObject();
			JSONObject dataStatus = new JSONObject();
			dataStatus.put("status",data);
			dataObject.put(deviceId, dataStatus.toJSONString());
			telemetryDataPoint.deviceData = dataObject.toJSONString();		
		}
		else if((specificEntity.getType().equals("Gladius_Parent"))&&(isGladiusChild==true))
		{
			telemetryDataPoint.n="data";
			telemetryDataPoint.s="123";
			telemetryDataPoint.deviceData = data;		
		}
		String msgStr = telemetryDataPoint.serialize();
		Message msg = new Message(msgStr);
		System.out.println(msgStr);
		Object lockobj = new Object();
		EventCallback callback = new EventCallback();
		List<Device> devices = DeviceClientSingleton.getDeviceList();
		Iterator<Device> iter = devices.iterator();
		int index = 0;
		while (iter.hasNext()) {

			Device element = iter.next();
			if (element.getDeviceId().equals(deviceId)) {
				break;
			}
			++index;
		}
		DeviceClientSingleton.getInstance(index).sendEventAsync(msg, callback, lockobj);
		synchronized (lockobj) {
			lockobj.wait();
		}
		Thread.sleep(1000);
		// client.close();
	}

	public List<Object> getAllDevices() throws Exception {
		List<Object> returnAllDevices=new ArrayList<Object>();
		HashMap<String, String> allDevices = new HashMap<String, String>();
		HashMap<String,String> gladiusChildDevices=new HashMap<String, String>();
		RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
		List<Device> devices = registryManager.getDevices(10000);
		System.out.println("size " + devices.size());
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the table client.
		CloudTableClient tableClient = storageAccount.createCloudTableClient();

		// Create a cloud table object for the table.
		CloudTable cloudTable = tableClient.getTableReference("DeviceList");

		for (Device dev : devices) {
			TableOperation retrieveData = TableOperation.retrieve("LedIotSolution",dev.getDeviceId(),
					DeviceEntity.class);
			
			System.out.println("checking for device "+dev.getDeviceId());
			// Submit the operation to the table service and get the specific
			// entity.
			DeviceEntity specificEntity = cloudTable.execute(retrieveData).getResultAsType();
			System.out.println("device type is"+specificEntity.getType());
			if(specificEntity.getType().equals("Gladius_Child"))
			{
				System.out.println("Inside GladiusChild");
				System.out.println("Parent Gladius is "+specificEntity.getGladiusParentId());
				gladiusChildDevices.put(dev.getDeviceId(),specificEntity.getGladiusParentId());
			
			}
			else
			{
				allDevices.put(dev.getDeviceId(), specificEntity.getType());
			}
		}
		returnAllDevices.add(allDevices);
		returnAllDevices.add(gladiusChildDevices);
		return returnAllDevices;
	}
	public List<Device> getNonChildDevices() throws Exception {
		RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
		List<Device> devices = registryManager.getDevices(10000);
		List<Device> nonGladiusDevices=new ArrayList<Device>();
		System.out.println("size " + devices.size());
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

		// Create the table client.
		CloudTableClient tableClient = storageAccount.createCloudTableClient();

		// Create a cloud table object for the table.
		CloudTable cloudTable = tableClient.getTableReference("DeviceList");

		for (Device dev : devices) {
			TableOperation retrieveData = TableOperation.retrieve("LedIotSolution",dev.getDeviceId(),
					DeviceEntity.class);
			
			// Submit the operation to the table service and get the specific
			// entity.
			DeviceEntity specificEntity = cloudTable.execute(retrieveData).getResultAsType();
			System.out.println("device type is"+specificEntity.getType());
			if(specificEntity.getType().equals("Gladius_Child"))
			{
			}
			else
			{
				nonGladiusDevices.add(dev);
			}
		}
		
		return nonGladiusDevices;
	}

	public List<String> getCommandStatus() {
		// TODO Auto-generated method stub
		List<Device> devices = DeviceClientSingleton.getDeviceList();
		List<String> commandStatus = new ArrayList<String>();
		for (int i = 0; i < devices.size(); i++) {
			String deviceData = DeviceClientSingleton.getDeviceData(devices.get(i).getDeviceId());
			commandStatus.add(deviceData);
			if ((!deviceData.equals("")) && (!deviceData.equals(" "))) {
				DeviceClientSingleton.removeDeviceData(devices.get(i).getDeviceId());
			}

		}
		return commandStatus;
	}

}