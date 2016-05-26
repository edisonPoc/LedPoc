package com.mindtree.handler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.microsoft.azure.iot.service.sdk.Device;
import com.microsoft.azure.iot.service.sdk.RegistryManager;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.mindtree.entity.DeviceDataCounter;
import com.mindtree.entity.DeviceEntity;
import com.mindtree.serviceImpl.DeviceServiceImpl;

public class DeviceClientSingleton {
    private static List<DeviceClient> instances;
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
	private static boolean[] isClientOpen=null;
	private static List<DeviceDataCounter> deviceDataList=null;
	private static List<Device> deviceList=null;
   private static DeviceServiceImpl deviceServiceImpl=new DeviceServiceImpl();
	/**
     * A private Constructor prevents any other class from
     * instantiating.
     */
    private DeviceClientSingleton() {
        // nothing to do this time
    }

    /**
     * The Static initializer constructs the instance at class
     * loading time; this is to simulate a more involved
     * construction process (it it were really simple, you'd just
     * use an initializer)
     */
    static {
        try {
        	instances=new ArrayList<DeviceClient>();
        	deviceList=deviceServiceImpl.getNonChildDevices();
        	
 			System.out.println("Number of devices : "+deviceList.size());
 			String deviceId="";
 			String hostName="LedIotSolution.azure-devices.net";
 			String deviceKey="";
 			String connString="";
 			DeviceClient instance=null;
 			for(int i=0;i<deviceList.size();i++)
 			{
 				Device dev=deviceList.get(i);
 				System.out.println("checking for device "+dev.getDeviceId());
 				deviceId=dev.getDeviceId();
 				deviceKey=dev.getPrimaryKey();
 					connString = "HostName="+hostName+";DeviceId="+deviceId+";SharedAccessKey="+deviceKey;
 					System.out.println(connString);
 	 			    instance = new DeviceClient(connString, protocol);
 	 			    
 	 			   instances.add(instance);	
 				
 			}
			isClientOpen=new boolean[instances.size()];
			for(int i=0;i<isClientOpen.length;i++)
			{
				isClientOpen[i]=false;
			}
			deviceDataList=new ArrayList<DeviceDataCounter>();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /** Static 'instance' method */
    public static DeviceClient getInstance(int index) {
    	System.out.println("index is"+index);
        return instances.get(index);
    }

    // other methods protected by singleton-ness would be here...
    /** A simple demo method */
    public String demoMethod() {
        return "demo";
    }
    public static void openClient(int index) throws IOException
    {
    	DeviceClientSingleton.instances.get(index).open();
    	DeviceClientSingleton.isClientOpen[index]=true;
    }
    public static void closeClient(int index) throws IOException
    {
    	DeviceClientSingleton.instances.get(index).close();
    	DeviceClientSingleton.isClientOpen[index]=false;
    }
    public static boolean isClientOpen(int index)
    {
    	return DeviceClientSingleton.isClientOpen[index];
    }
    public static void addDeviceData(DeviceDataCounter deviceData)
    {
    	DeviceClientSingleton.deviceDataList.add(deviceData);
    	}
    public static int totalDeviceClients()
    {
    	return DeviceClientSingleton.instances.size();
    	}
    public static String getDeviceData(String deviceId)
    {
    	System.out.println("getting data for device Id "+deviceId);
    	Iterator<DeviceDataCounter> itr=DeviceClientSingleton.deviceDataList.iterator();
    	List<DeviceDataCounter> dataCounter=new ArrayList<DeviceDataCounter>();
    	 for (Iterator<DeviceDataCounter> iterator = DeviceClientSingleton.deviceDataList.iterator(); iterator.hasNext(); ) {
    		 DeviceDataCounter element = iterator.next();
    		 if(element.deviceId.equals(deviceId))
             {
             	dataCounter.add(element);
             }
    		}
    	 System.out.println("data counter size"+dataCounter.size());
    	 if(dataCounter.size()>0)
    	 {
    		 return dataCounter.get(dataCounter.size()-1).status;
    	 }
         return "";
    }
    public static void removeDeviceData(String deviceId)
    {
    	for (Iterator<DeviceDataCounter> iterator = DeviceClientSingleton.deviceDataList.iterator(); iterator.hasNext(); ) {
   		 DeviceDataCounter element = iterator.next();
   		 if(element.deviceId.equals(deviceId))
   		 {
   			 iterator.remove();
   		 }
   		}
    }
    public static List<Device> getDeviceList() {
		return deviceList;
	}

	public static void setDeviceList(List<Device> deviceList) {
		DeviceClientSingleton.deviceList = deviceList;
	}
	
}
