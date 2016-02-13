package com.dc.logoserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Server Application. Listens to commands from the client and processes them
 */
public class LOGOServer {
	protected ServerSocket serverSocket;
	protected GpioController gpio;
	protected Map<String, GpioPinDigitalOutput> pins;

	/**
	 * Creates a new Socket on the specified port
	 * 
	 * @param port
	 *            Port to be opened
	 * @throws IOException
	 *             If there is a problem opening the port
	 */
	public LOGOServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("Opening socket on port " + port);

		gpio = GpioFactory.getInstance();
		pins = new HashMap<String, GpioPinDigitalOutput>();
	}

	/**
	 * Gets a pin with the specified number. Pins are cached, so this method can
	 * be called as many times as needed
	 * 
	 * @param number
	 *            Number of the pin to get
	 * @return The pin which was requested
	 */
	public GpioPinDigitalOutput getPin(int number) {
		String pinName = "GPIO " + number;
		GpioPinDigitalOutput pin;

		if (pins.containsKey(pinName)) {
			pin = pins.get(pinName);
		} else {
			pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(pinName));
			pins.put(pin.getName(), pin);
		}

		return pin;
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
		GpioPinDigitalOutput in1 = getPin(23);
		GpioPinDigitalOutput in2 = getPin(27);
		GpioPinDigitalOutput in3 = getPin(28);
		GpioPinDigitalOutput in4 = getPin(29);

		// Turn each pin on, then turn each pin off, to show they are all
		// connected properly
		in1.high();
		Thread.sleep(500);
		in2.high();
		Thread.sleep(500);
		in3.high();
		Thread.sleep(500);
		in4.high();
		Thread.sleep(500);
		in1.low();
		Thread.sleep(500);
		in2.low();
		Thread.sleep(500);
		in3.low();
		Thread.sleep(500);
		in4.low();

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
					int distance = Integer.parseInt(command.split(" ")[1]) * 10;

					// Make sure all inputs are off before we send the signal
					in1.low();
					in2.low();
					in3.low();
					in4.low();

					in4.high();
					for (int x = 0; x < distance; x++) {
						System.out.println("Loop " + x);
						Thread.sleep(speed);
						in3.high();
						Thread.sleep(speed);
						in4.low();
						Thread.sleep(speed);
						in2.high();
						Thread.sleep(speed);
						in3.low();
						Thread.sleep(speed);
						in1.high();
						Thread.sleep(speed);
						in2.low();
						Thread.sleep(speed);
						in4.high();
						Thread.sleep(speed);
						in1.low();
					}

					// Make sure all inputs are off after we send the signal
					in1.low();
					in2.low();
					in3.low();
					in4.low();
				}

				response = "LOGO commands successfully executed";
			} else if (input.matches("^[0-9]{1,2};x$")) {
				// If a number is received, toggle the pin with that number
				int lastSemiColon = input.lastIndexOf(";");
				String pinNumber = input.substring(0, lastSemiColon);
				GpioPinDigitalOutput pin = getPin(Integer.parseInt(pinNumber));

				pin.toggle();

				response = pin.getName() + " toggled successfully";
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
				in1.high();
				in2.high();
				in3.high();
				in4.high();
				Thread.sleep(1000);
				in1.low();
				in2.low();
				in3.low();
				in4.low();

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