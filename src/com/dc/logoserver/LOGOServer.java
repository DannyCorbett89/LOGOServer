package com.dc.logoserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LOGOServer {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = new ServerSocket(83);
		boolean listen = true;

		while (listen) {
			Socket connection = serverSocket.accept();

			// TODO: Turn green LED on
			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			StringBuffer process = new StringBuffer();
			int character;

			while ((character = isr.read()) != 13) {
				process.append((char) character);
			}

			// TODO: Parse command. If valid, run motors. Otherwise, flash red
			// LED and send failure response to client
			String input = process.toString().toLowerCase();
			String response;

			if (input.equalsIgnoreCase("green on")) {
				// TODO: turn green led on
				response = "Green LED successfully switched on";
			} else if (input.equalsIgnoreCase("green off")) {
				// TODO: turn green led off
				response = "Green LED successfully switched off";
			} else if (input.equalsIgnoreCase("red on")) {
				// TODO: turn red led on
				response = "Red LED successfully switched on";
			} else if (input.equalsIgnoreCase("red off")) {
				// TODO: turn red led off
				response = "Red LED successfully switched off";
			} else if (input.matches("^((fd|rt|lt) [0-9]+;)+x$")) {
				int lastSemiColon = input.lastIndexOf(";");
				String[] commands = input.substring(0, lastSemiColon).split(";");

				for (String command : commands) {
					System.out.println("Executing command: " + command);
				}

				response = "LOGO commands successfully executed";
			} else {
				response = "Invalid input: " + input;
			}

			BufferedOutputStream os = new BufferedOutputStream(connection.getOutputStream());
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(response + (char) 13);
			osw.flush();

			// TODO: Turn green LED off
		}

		serverSocket.close();
	}
}
