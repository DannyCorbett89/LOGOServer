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

public class LOGOServer {
	public static void main(String[] args) throws IOException, InterruptedException {
		ServerSocket serverSocket = new ServerSocket(83);
		boolean listen = true;

		GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput in1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23);
		GpioPinDigitalOutput in2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);
		GpioPinDigitalOutput in3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28);
		GpioPinDigitalOutput in4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29);
		Map<String, GpioPinDigitalOutput> pins = new HashMap<String, GpioPinDigitalOutput>();

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

			if (input.matches("^((fd|rt|lt) [0-9]+;)+x$")) {
				int lastSemiColon = input.lastIndexOf(";");
				String[] commands = input.substring(0, lastSemiColon).split(";");

				for (String command : commands) {
					System.out.println("Executing command: " + command);
					int speed = 1;
					int distance = Integer.parseInt(command.split(" ")[1]) * 100;

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
				int lastSemiColon = input.lastIndexOf(";");
				String pinNumber = input.substring(0, lastSemiColon);
				String pinName = "GPIO " + Integer.parseInt(pinNumber);
				GpioPinDigitalOutput pin;

				if (pins.containsKey(pinName)) {
					pin = pins.get(pinName);
				} else {
					pin = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(pinName));
				}

				pin.toggle();

				response = pinName + " toggled successfully";
				System.out.println(response);
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
}