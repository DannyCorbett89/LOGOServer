package com.dc.logoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * TODO: Experimental, untested
 */
public class BluetoothInputReceiver implements InputReceiver {
	public BluetoothInputReceiver() throws IOException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: " + localDevice.getBluetoothAddress());
		System.out.println("Name: " + localDevice.getFriendlyName());
		System.out.println("Stack: " + LocalDevice.getProperty("bluecove.stack"));
		localDevice.setDiscoverable(DiscoveryAgent.GIAC);
	}

	@Override
	public String receiveInput() throws IOException { // Create a UUID for SPP
		UUID uuid = new UUID("04c6032b00004000800000805f9b34fc", false);
		// Create the servicve url
		String connectionString = "btspp://localhost:" + uuid + ";name=LOGOServer";

		// open server url
		StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector.open(connectionString);

		// Wait for client connection
		System.out.println("\nServer Started. Waiting for clients to connect...");
		StreamConnection connection = streamConnNotifier.acceptAndOpen();

		// read string from spp client
		InputStream inStream = connection.openInputStream();
		BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
		String input = bReader.readLine();

		streamConnNotifier.close();

		return input;
	}

	@Override
	public void close() {
		// Not used
	}
}
