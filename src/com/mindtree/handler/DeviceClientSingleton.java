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
import com.mindtree.entity.DeviceDataCounter;

public class DeviceClientSingleton {
    private static List<DeviceClient> instances;
    private static IotHubClientProtocol protocol = IotHubClientProtocol.AMQPS;
	private static boolean[] isClientOpen=null;
	private static List<DeviceDataCounter> deviceDataList=null;
	private static List<Device> deviceList=null;
   
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
        	String connectionString = "HostName=LedIotSuite.azure-devices.net;SharedAccessKeyName=iothubowner;SharedAccessKey=0mpAS5WMeuJJ2g6jGyvdWBidmIzPONV5ovT3HOfoUA8=";
        	
        	RegistryManager registryManager = RegistryManager.createFromConnectionString(connectionString);
        	deviceList=registryManager.getDevices(10000);
 			System.out.println("Number of devices : "+deviceList.size());
 			String deviceId="";
 			String hostName="LedIotSuite.azure-devices.net";
 			String deviceKey="";
 			String connString="";
 			DeviceClient instance=null;
 			for(int i=0;i<deviceList.size();i++)
 			{
 				Device dev=deviceList.get(i);
 				deviceId=dev.getDeviceId();
 				deviceKey=dev.getPrimaryKey();
 				connString = "HostName="+hostName+";DeviceId="+deviceId+";SharedAccessKey="+deviceKey;
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
    	Iterator<DeviceDataCounter> itr=DeviceClientSingleton.deviceDataList.iterator();
    	List<DeviceDataCounter> dataCounter=new ArrayList<DeviceDataCounter>();
    	 while(itr.hasNext()) {
            DeviceDataCounter element = (DeviceDataCounter) itr.next();
            if(element.deviceId.equals(deviceId))
            {
            	dataCounter.add(element);
            }
         }
    	 if(dataCounter.size()>0)
    	 {
    		 return dataCounter.get(dataCounter.size()-1).status;
    	 }
         return "";
    }
    public static List<Device> getDeviceList() {
		return deviceList;
	}

	public static void setDeviceList(List<Device> deviceList) {
		DeviceClientSingleton.deviceList = deviceList;
	}
	
}
