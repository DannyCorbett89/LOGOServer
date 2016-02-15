package com.dc.logoserver.robot;

import com.dc.logoserver.robot.pinstates.Sequence;

/**
 * Robot implementation which prints the commands to the console
 */
public class PrintRobot implements Robot {
	@Override
	public void toggle(int pinNumber) {
		System.out.println("Toggling Pin Number: " + pinNumber);
	}

	@Override
	public void fd(int distance, int speed) throws InterruptedException {
		System.out.println("Moving forwards: " + distance);
	}

	@Override
	public void rt(int degrees, int speed) throws InterruptedException {
		System.out.println("Turning right: " + degrees + " degrees");
	}

	@Override
	public void lt(int degrees, int speed) throws InterruptedException {
		System.out.println("Turning left: " + degrees + " degrees");
	}

	@Override
	public void execute(Sequence sequence, int delay) throws InterruptedException {
		// Not Implemented
	}
}
