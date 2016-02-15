package com.dc.logoserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.dc.logoserver.robot.LOGORobot;
import com.dc.logoserver.robot.PrintRobot;
import com.dc.logoserver.robot.Robot;
import com.dc.logoserver.robot.pinstates.PingSequence;
import com.dc.logoserver.robot.pinstates.StartupSequence;

/**
 * Server Application. Listens to commands from the client and processes them
 */
public class LOGOServer {
	protected ServerSocket serverSocket;
	protected Robot robot;

	/**
	 * Creates a new Socket on the specified port
	 * 
	 * @param port
	 *            Port to be opened
	 * @throws IOException
	 *             If there is a problem opening the port
	 */
	public LOGOServer(int port) throws IOException {
		this(port, true);
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
	public LOGOServer(int port, boolean useRobot) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Opening socket on port " + port);

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
			Socket connection = serverSocket.accept();

			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			StringBuffer process = new StringBuffer();
			int character;

			while ((character = isr.read()) != 13) {
				process.append((char) character);
			}

			String input = process.toString().toLowerCase();
			String response;
			System.out.println("Input: " + input);

			if (input.matches("^((fd|rt|lt) [0-9]+;)+x$")) {
				int lastSemiColon = input.lastIndexOf(";");
				String[] commands = input.substring(0, lastSemiColon).split(";");

				for (String command : commands) {
					System.out.println("Executing command: " + command);
					int speed = 2;
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
			} else if (input.matches("^[0-9]{1,2};x$")) {
				// If a number is received, toggle the pin with that number
				int lastSemiColon = input.lastIndexOf(";");
				String pinNumber = input.substring(0, lastSemiColon);
				robot.toggle(Integer.parseInt(pinNumber));

				response = pinNumber + " toggled successfully";
				System.out.println(response);
			} else if (input.matches("^exit;x$")) {
				// If the word "exit" is received, set listen to false which
				// will break the loop, send the response, and then exit the
				// program
				listen = false;
				response = "Exiting";
			} else if (input.matches("^ping;x$")) {
				// If the word "ping" is received, turn all pins on for 1
				// second, then turn them all off. Send a response to the client
				robot.execute(new PingSequence(), 1000);

				response = "pong";
			} else {
				response = "Invalid input: " + input;
			}

			BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(response + (char) 13);
			osw.flush();
		}

		serverSocket.close();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int port;

		// Port defaults to 83, but allow it to be passed in as an argument
		if (args.length == 0) {
			port = 83;
		} else {
			port = Integer.parseInt(args[0]);
		}

		LOGOServer server = new LOGOServer(port);
		server.start();
	}
}