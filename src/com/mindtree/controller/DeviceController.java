package com.mindtree.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.storage.StorageException;
import com.mindtree.serviceImpl.DeviceServiceImpl;
@Controller
public class DeviceController {
	DeviceServiceImpl deviceService=null;
	@RequestMapping(value = "/sendDeviceData", method = RequestMethod.POST)
	public ModelAndView sendDeviceData(@RequestParam("deviceData") String data,@RequestParam("deviceId") String deviceId) throws URISyntaxException, IOException, InterruptedException, InvalidKeyException, StorageException {
		ModelAndView model = new ModelAndView();
		deviceService=new DeviceServiceImpl();
		System.out.println("Sending data to Azure IOT Hub");
		deviceService.sendDeviceData(data,deviceId);
		model.addObject("controldata","");
		model.setViewName("LightBulb.jsp");
		return model;
	}
	@RequestMapping(value = "/getDeviceList", method = RequestMethod.GET)
	public ModelAndView getDeviceList() throws Exception{
		ModelAndView model = new ModelAndView();
		deviceService=new DeviceServiceImpl();
		System.out.println("Getting list of devices running on the Azure IOT Hub");
		HashMap<String,String> devices=deviceService.getAllDevices();
		model.addObject("devices",devices);
		model.setViewName("LightBulb.jsp");
		return model;
	}
	@ResponseBody
	@RequestMapping(value = "/getCommand", method = RequestMethod.GET)
	public String getCommand() throws URISyntaxException, IOException, InterruptedException {
		System.out.println("Getting command from Azure IOT Hub");
		deviceService=new DeviceServiceImpl();
		List<String> data=deviceService.getCommandStatus();
		System.out.println(data);
		String json = new Gson().toJson(data);
		return json;
	}
}
