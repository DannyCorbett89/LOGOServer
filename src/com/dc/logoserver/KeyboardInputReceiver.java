package com.dc.logoserver;

import java.util.Scanner;

public class KeyboardInputReceiver implements InputReceiver {
	protected Scanner scanner;

	public KeyboardInputReceiver() {
		scanner = new Scanner(System.in);
	}

	@Override
	public String receiveInput() {
		return scanner.nextLine();
	}

	@Override
	public void close() {
		scanner.close();
	}

}
