package com.dc.logoserver;

import com.dc.logoserver.robot.LOGORobot;
import com.dc.logoserver.robot.PrintRobot;
import com.dc.logoserver.robot.Robot;
import com.dc.logoserver.robot.pinstates.PingSequence;
import com.dc.logoserver.robot.pinstates.ShutdownSequence;
import com.dc.logoserver.robot.pinstates.StartupSequence;

/**
 * Server Application. Listens to commands from the client and processes them
 */
public class LOGOServer {
	protected Robot robot;
	protected InputReceiver receiver;

	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public void setReceiver(InputReceiver receiver) {
		this.receiver = receiver;
	}

	public InputReceiver getReceiver() {
		return receiver;
	}

	public void start() throws Exception {
		if (robot == null) {
			robot = new LOGORobot();
		}

		if (receiver == null) {
			throw new Exception("No InputReceiver defined");
		}

		boolean listen = true;

		// Turn each pin on, then turn each pin off, to show they are all
		// connected properly
		robot.execute(new StartupSequence(), 200);

		while (listen) {
			System.out.println("Enter Command: ");
			String input = receiver.receiveInput();

			String response;

			if (input.matches("^([;]?(fd|rt|lt) [0-9]+)+$")) {
				String[] commands = input.split(";");

				for (String command : commands) {
					System.out.println("Executing command: " + command);
					int speed = 1;
					String[] parts = command.split(" ");
					int distance = Integer.parseInt(parts[1]);

					String direction = parts[0];

					if (direction.equals("fd")) {
						robot.fd(distance, speed);
					} else if (direction.equals("lt")) {
						robot.lt(distance, speed);
					} else if (direction.equals("rt")) {
						robot.rt(distance, speed);
					}
				}

				response = "LOGO commands successfully executed";
			} else if (input.matches("^[0-9]{1,2}$")) {
				// If a number is received, toggle the pin with that number
				int lastSemiColon = input.lastIndexOf(";");
				String pinNumber = input.substring(0, lastSemiColon);
				robot.toggle(Integer.parseInt(pinNumber));

				response = pinNumber + " toggled successfully";
				System.out.println(response);
			} else if (input.matches("^exit$")) {
				// If the word "exit" is received, set listen to false which
				// will break the loop, send the response, and then exit the
				// program
				listen = false;
				response = "Exiting";

				// Visual confirmation of shutdown
				robot.execute(new ShutdownSequence(), 200);
			} else if (input.matches("^ping$")) {
				// If the word "ping" is received, turn all pins on for 1
				// second, then turn them all off. Send a response to the client
				robot.execute(new PingSequence(), 1000);

				response = "pong";
			} else {
				response = "Invalid input: " + input;
			}

			System.out.println();
		}

		receiver.close();
	}

	public static void main(String[] args) throws Exception {
		LOGOServer server = new LOGOServer();

		for (int x = 0; x < args.length; x++) {
			if (args[x].equals("-wifi")) {
				server.setReceiver(new WifiInputReceiver());
			} else if (args[x].equals("-bluetooth")) {
				server.setReceiver(new BluetoothInputReceiver());
			} else if (args[x].equals("-print")) {
				server.setRobot(new PrintRobot());
			}
		}

		if (server.getReceiver() == null) {
			server.setReceiver(new KeyboardInputReceiver());
		}

		// Port needs to be checked last, regardless of which order the
		// arguments are in, so that the WifiInputReceiver has definitely
		// already been set
		for (int x = 0; x < args.length; x++) {
			if (args[x].equals("-port")) {
				InputReceiver receiver = server.getReceiver();

				if (receiver instanceof WifiInputReceiver) {
					int port = Integer.parseInt(args[++x]);
					((WifiInputReceiver) receiver).setPort(port);
				}
			}
		}

		server.start();
	}
}