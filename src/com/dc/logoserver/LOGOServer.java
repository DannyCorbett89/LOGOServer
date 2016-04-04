package com.dc.logoserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.dc.logoserver.robot.LOGORobot;
import com.dc.logoserver.robot.PrintRobot;
import com.dc.logoserver.robot.Robot;
import com.dc.logoserver.robot.pinstates.PingSequence;
import com.dc.logoserver.robot.pinstates.StartupSequence;

/**
 * Server Application. Listens to commands from the client and processes them
 */
public class LOGOServer {
	protected Robot robot;

	/**
	 * Creates a new Socket on the specified port
	 * 
	 * @param port
	 *            Port to be opened
	 * @throws IOException
	 *             If there is a problem opening the port
	 */
	public LOGOServer() throws IOException {
		this(true);
	}

	/**
	 * Creates a new Socket on the specified port
	 * 
	 * @param port
	 *            Port to be opened
	 * @param useRobot
	 *            Switches between sending commands to the robot, or printing
	 *            them out to the screen
	 * @throws IOException
	 *             If there is a problem opening the port
	 */
	public LOGOServer(boolean useRobot) throws IOException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: " + localDevice.getBluetoothAddress());
		System.out.println("Name: " + localDevice.getFriendlyName());
		localDevice.setDiscoverable(DiscoveryAgent.GIAC);

		if (useRobot) {
			robot = new LOGORobot();
		} else {
			robot = new PrintRobot();
		}
	}

	/**
	 * Starts listening on the specified port. Will flash all pins to show that
	 * it is working, then will wait for a command from the client
	 * 
	 * @throws InterruptedException
	 *             If there is a problem when pausing the program between pin
	 *             flashes
	 * @throws IOException
	 *             If there is a problem reading from the socket
	 */
	public void start() throws InterruptedException, IOException {
		boolean listen = true;

		// Turn each pin on, then turn each pin off, to show they are all
		// connected properly
		robot.execute(new StartupSequence(), 500);

		while (listen) {
			// Create a UUID for SPP
			UUID uuid = new UUID("04c6032b00004000800000805f9b34fc", false);
			// Create the servicve url
			String connectionString = "btspp://localhost:" + uuid + ";name=Sample SPP Server";

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

			String response;
			System.out.println("Input: " + input);

			if (input.matches("^((fd|rt|lt) [0-9]+;)+$")) {
				int lastSemiColon = input.lastIndexOf(";");
				String[] commands = input.substring(0, lastSemiColon).split(";");

				for (String command : commands) {
					System.out.println("Executing command: " + command);
					int speed = 1;
					String[] parts = command.split(" ");
					int distance = Integer.parseInt(parts[1]) * 10;

					String direction = parts[0];

					switch (direction) {
					case "fd":
						robot.fd(distance, speed);
						break;
					case "lt":
						robot.lt(distance, speed);
						break;
					case "rt":
						robot.rt(distance, speed);
						break;

					default:
						break;
					}
				}

				response = "LOGO commands successfully executed";
			} else if (input.matches("^[0-9]{1,2};$")) {
				// If a number is received, toggle the pin with that number
				int lastSemiColon = input.lastIndexOf(";");
				String pinNumber = input.substring(0, lastSemiColon);
				robot.toggle(Integer.parseInt(pinNumber));

				response = pinNumber + " toggled successfully";
				System.out.println(response);
			} else if (input.matches("^exit;$")) {
				// If the word "exit" is received, set listen to false which
				// will break the loop, send the response, and then exit the
				// program
				listen = false;
				response = "Exiting";
			} else if (input.matches("^ping;$")) {
				// If the word "ping" is received, turn all pins on for 1
				// second, then turn them all off. Send a response to the client
				robot.execute(new PingSequence(), 1000);

				response = "pong";
			} else {
				response = "Invalid input: " + input;
			}

			OutputStream os = connection.openOutputStream();
			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(os));
			bWriter.write(response + "\r\n");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		LOGOServer server = new LOGOServer(false);
		server.start();
	}
}