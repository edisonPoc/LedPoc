<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
 flag=0;
 </script>
 <div align="center">
<h1>Simulated device integration with Azure IOT Hub</h1>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
   <div>
   <c:forEach items="${devices}" var="device" varStatus="loop">
   <h3>${device}
		<img style="margin-left: 0%;" class="${loop.index}" id="${device}" onclick="changeImage('${device}')" src="pic_bulboff.gif" width="100" height="180">
  	</h3>
  	<br />
	</c:forEach>
   </div>
  	<br>
   <p><b>Click the light bulb to turn on/off the light.</b></p> 
   <br><br>
<!-- <p>The status of light bulb is <input type="text" id="flagValue" value="0"></p>
 --></div>
  <script>
  deviceStatus=0;
  var myVar = setInterval(myTimer, 5000);

  function myTimer() {
	  
	  $.ajax({
	  		type : 'GET',
	  		url : 'getCommand',
	  		data : {},
	  		success : function(response) {
	  			console.log(response);
	  			commands=JSON.parse(response);
	  			console.log(commands.length);
	  			 for(var i=0;i<commands.length;i++)
	  				 {
	  				 console.log(commands[i]);
	  			if((commands[i]!==" ")&&(commands[i]!==""))
	  				{
	  				console.log("Inside");
	  				var deviceValue =document.getElementsByClassName(i);
	  				console.log(deviceValue[0].id);
	  			changeImageFromButton(commands[i],deviceValue[0].id);
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
	  deviceStatus=arguments[0];
	  deviceId=arguments[1];
	  var imageObject=document.getElementById(deviceId);
      if ((deviceStatus==0)||(deviceStatus===0)) {
    	  imageObject.src = "pic_bulboff.gif"; 
         //document.getElementById("flagValue").value="0";
         deviceStatus=0;
      } 
     else if ((deviceStatus==1)||(deviceStatus===1)){ 
    	 
       imageObject.src="pic_bulbon.gif";
        //document.getElementById("flagValue").value="1";  
        deviceStatus=1;
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
		},async : false
 	});     
  }
   function changeImage() { 
	   console.log("The deviceId is "+arguments[0]);
	   var deviceId=arguments[0];
      var image = document.getElementById('myImage');
      if (image.src.match("bulbon")) {
         image.src = "pic_bulboff.gif"; 
         //document.getElementById("flagValue").value="0";
         deviceStatus=0;
      } 
     else { 
        image.src = "pic_bulbon.gif"; 
        //document.getElementById("flagValue").value="1";  
        deviceStatus=1;
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
		},async : false
  	});  
      } 
 </script>

</body>
</html>