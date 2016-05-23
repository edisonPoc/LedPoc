<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Light Bulb POC</title>
</head>
<body>
	<script>
		/* $(document).ready(function()
		 {
		 deviceCounter=0;
		 deviceCounter=${devices};
		 } */
		flag = 0;
	</script>
	<div align="center">
		<h1>Simulated device integration with Azure IOT Hub</h1>
		<script
			src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<div>
			<c:forEach items="${devices}" var="device" varStatus="loop">
				<h3 id="header${loop.index}">
					<b id="device${loop.index}"></b> <img style="margin-left: 0%;"
						class="${loop.index}" id="${device}"
						src="pic_bulboff.gif"
						width="100" height="180">
				</h3>
				<br />
			</c:forEach>
		</div>
		<br>
		<p>
			<b>Click the light bulb to turn on/off the light.</b>
		</p>
		<input id="deviceList" type="hidden" value="${devices}" /> <br>
		<br>
		<!-- <p>The status of light bulb is <input type="text" id="flagValue" value="0"></p>
 -->
	</div>
	<script>
		$(document)
				.ready(
						function() {
							console.log("Document Ready!");
							deviceListElements = document
									.getElementById("deviceList");
							deviceListElements = deviceListElements.value;
							console.log(deviceListElements);
							deviceListElements = deviceListElements.substring(
									1, deviceListElements.length - 1);
							deviceListElements = deviceListElements.split(", ");
							console.log(deviceListElements);
							deviceList = [];
							for (var i = 0; i < deviceListElements.length; i++) {
								var deviceListElementBreak = deviceListElements[i]
										.split("=");
								console.log(deviceListElementBreak[0] + " "
										+ deviceListElementBreak[1]);
								deviceList[i] = deviceListElementBreak[0];
								deviceName = document.getElementById("device"
										+ i);
								deviceName.innerHTML = deviceListElementBreak[0];
								headerItem = document.getElementById("header"
										+ i);
								console.log(headerItem);
								if (deviceListElementBreak[1] === 'Simulated') {
									headerItem.style.background = "#74fcf5";
								} else if (deviceListElementBreak[1] === 'Rasberry') {
									headerItem.style.background = "#d5ff80";
								}
								 else if (deviceListElementBreak[1] === 'Gladius') {
										headerItem.style.background = "#ff6666";
									}
								$
										.ajax({
											type : 'GET',
											url : 'http://lediotpoc.cloudapp.net/IotDeviceInformation/getDeviceStatus',
											data : {
												deviceID : deviceListElementBreak[0]
											},
											async : false,
											headers : {
												"Access-Control-Expose-Headers" : "Content-Disposition",
												"Access-Control-Allow-Credentials" : "true"
											},
											success : function(data) {
												if($.trim(data))
												{
												console
														.log("Device Status Recieved");
												console.log(data);
												dataParsed=JSON.parse(data);
												deviceStatus = dataParsed.status;
												if ((deviceStatus != "")
														&& (deviceStatus != " ")) {
													var inputType = document
															.getElementById(deviceListElements[i]);
													if (deviceStatus === '1') {
														inputType.src = "pic_bulbon.gif";
													} else if (deviceStatus === '0') {
														inputType.src = "pic_bulboff.gif";
													}
												}
												}
											},
											error : function(error) {
												console
														.log("Could not send the command");
											}
										});
							}
						});
		deviceStatus = 0;
		var myVar = setInterval(myTimer, 5000);

		function myTimer() {

			$.ajax({
				type : 'GET',
				url : 'getCommand',
				data : {},
				success : function(response) {
					console.log(response);
					commands = JSON.parse(response);
					console.log(commands.length);
					for (var i = 0; i < commands.length; i++) {
						console.log(commands[i]);
						if ((commands[i] !== " ") && (commands[i] !== "")) {
							console.log("Inside");
							var deviceValue = document
									.getElementsByClassName(i);
							console.log(deviceValue[0].id);
							changeImageFromButton(commands[i],
									deviceValue[0].id);
						}
					}
					console.log("Command successfully recieved");
				},
				error : function(error) {
					console.log("Could not recieve the command");
				}
			});
		}

		function changeImageFromButton() {
			deviceStatus = arguments[0];
			deviceComplete = arguments[1];
			var deviceDataBreak = deviceComplete.split("=");
			deviceId = deviceDataBreak[0];
	
			var imageObject = document.getElementById(deviceComplete);
			if ((deviceStatus == 0) || (deviceStatus === 0)) {
				imageObject.src = "pic_bulboff.gif";
				//document.getElementById("flagValue").value="0";
				deviceStatus = 0;
			} else if ((deviceStatus == 1) || (deviceStatus === 1)) {

				imageObject.src = "pic_bulbon.gif";
				//document.getElementById("flagValue").value="1";  
				deviceStatus = 1;
			}
			console.log("Making ajax request to send device values");
			$.ajax({
				type : 'POST',
				url : 'sendDeviceData',
				data : {
					deviceData : deviceStatus,
					deviceId : deviceId
				},
				success : function(data) {
					console.log("Data successfully sent");
				},
				error : function(error) {
					console.log("Could not make the api call");
				},
				async : false
			});
		}
			</script>

</body>
</html>