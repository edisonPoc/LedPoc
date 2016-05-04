/*// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.mindtree.handler;

import com.microsoft.azure.iothub.DeviceClient;
import com.microsoft.azure.iothub.IotHubClientProtocol;
import com.microsoft.azure.iothub.Message;
import com.mindtree.entity.Counter;
import com.mindtree.serviceImpl.DeviceServiceImpl;
import com.mindtree.serviceImpl.MessageCallback;
import com.microsoft.azure.iothub.IotHubMessageResult;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;



public class HandleMessages
{
	DeviceServiceImpl deviceService;
    *//** Used as a counter in the message callback. *//*
    protected static class Counter
    {
        protected int num;

        public Counter(int num)
        {
            this.num = num;
        }

        public int get()
        {
            return this.num;
        }

        public void increment()
        {
            this.num++;
        }

        @Override
        public String toString()
        {
            return Integer.toString(this.num);
        }
    }

    // Our MQTT doesn't support abandon/reject, so we will only display the messaged received
    // from IoTHub and return COMPLETE
    protected static class MessageCallbackMqtt implements com.microsoft.azure.iothub.MessageCallback
    {
        public IotHubMessageResult execute(Message msg, Object context)
        {
            Counter counter = (Counter) context;
            System.out.println(
                    "Received message " + counter.toString()
                            + " with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));

            counter.increment();

            return IotHubMessageResult.COMPLETE;
        }
    }

    *//**
     * Receives requests from an IoT Hub. Default protocol is to use
     * HTTPS transport.
     *
     * @param args args[0] = IoT Hub connection string; args[1] = protocol (one
     * of 'https', 'amqps', 'mqtt' or 'amqps_ws', optional).
     *//*
    public static void main(String[] args)
            throws IOException, URISyntaxException
    {
        System.out.println("Starting...");
        System.out.println("Beginning setup.");
        args=new String[2];
        args[0]="HostName=LedIotSuite.azure-devices.net;DeviceId=mydeviceLED;SharedAccessKey=D7ePoQrpW0NcE3HgxIWz1g==";
        args[1]="amqps";
        if (args.length <= 0 || 3 <= args.length)
        {
            System.out.format(
                    "Expected 1 or 2 arguments but received:\n%d. "
                            + "The program should be called with the: "
                            + "following args: "
                            + "[IoT Hub connection string] (https | amqps | mqtt | amqps_ws).\n",
                    args.length);
            return;
        }

        String connString = args[0];
        IotHubClientProtocol protocol;
        if (args.length == 1)
        {
            protocol = IotHubClientProtocol.HTTPS;
        }
        else
        {
            String protocolStr = args[1];
            if (protocolStr.equals("https"))
            {
                protocol = IotHubClientProtocol.HTTPS;
            }
            else if (protocolStr.equals("amqps"))
            {
                protocol = IotHubClientProtocol.AMQPS;
            }
            else if (protocolStr.equals("mqtt"))
            {
                protocol = IotHubClientProtocol.MQTT;
            }
            else
            {
                System.out.format(
                        "Expected argument 1 to be one of 'https', 'amqps', 'mqtt' or 'amqps_ws'"
                                + "but received %s. The program should be "
                                + "called with the: following args: "
                                + "[IoT Hub connection string] (https | amqps | mqtt | amqps_ws)."
                                + "\n",
                        protocolStr);
                return;
            }
        }

        System.out.println("Successfully read input parameters.");
        System.out.format("Using communication protocol %s.\n",
                protocol.name());

        DeviceClient client = DeviceClientSingleton.getInstance();

        System.out.println("Successfully created an IoT Hub client.");

        if (protocol == IotHubClientProtocol.MQTT)
        {
            MessageCallbackMqtt callback = new MessageCallbackMqtt();
            Counter counter = new Counter(0);
            client.setMessageCallback(callback, counter);
        }
        else
        {
            MessageCallback callback = new MessageCallback();
            callback.setDeviceClient(client);
            Counter counter = new Counter(0);
            client.setMessageCallback(callback, counter);
        }
        System.out.println("Successfully set message callback.");

        //client.open();
        DeviceClientSingleton.openClient();

        System.out.println("Opened connection to IoT Hub.");

        System.out.println("Beginning to receive messages...");
        System.out.println("Press any key to exit...");
        System.out.println("Status of client in HandleMesg is "+DeviceClientSingleton.isClientOpen());
		
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();

        DeviceClientSingleton.closeClient();

        System.out.println("Shutting down...");
    }
}*/