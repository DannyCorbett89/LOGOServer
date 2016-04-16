package com.dc.logoserver;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

/**
 * TODO: Experimental, untested
 */
public class WifiInputReceiver implements InputReceiver {
	protected ServerSocket serverSocket;
	protected int port;

	public WifiInputReceiver() {
		try {
			for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces
					.hasMoreElements();) {
				final NetworkInterface cur = interfaces.nextElement();

				if (cur.isLoopback()) {
					continue;
				}

				System.out.println("interface " + cur.getName());

				for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
					final InetAddress inet_addr = addr.getAddress();

					if (!(inet_addr instanceof Inet4Address)) {
						continue;
					}

					System.out.println("  address: " + inet_addr.getHostAddress());
				}
			}

			System.out.println("Opening socket on port " + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String receiveInput() {
		try {
			ServerSocket serverSocket = new ServerSocket(port);

			Socket connection = serverSocket.accept();

			BufferedInputStream is = new BufferedInputStream(connection.getInputStream());
			InputStreamReader isr = new InputStreamReader(is);
			StringBuffer process = new StringBuffer();
			int character;

			while ((character = isr.read()) != 13) {
				process.append((char) character);
			}

			String input = process.toString().toLowerCase();

			serverSocket.close();

			return input;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void close() {
		// Not used
	}

	public void setPort(int port) {
		this.port = port;
	}

}
