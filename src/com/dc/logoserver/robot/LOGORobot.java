package com.dc.logoserver.robot;

import com.dc.logoserver.robot.pinstates.BackwardSequence;
import com.dc.logoserver.robot.pinstates.ForwardSequence;
import com.dc.logoserver.robot.pinstates.Sequence;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * {@link Robot} implementation which sends commands to physical hardware (GPIO
 * Pins)
 */
public class LOGORobot implements Robot {
	protected Motor leftMotor;
	protected Motor rightMotor;

	public LOGORobot() {
		leftMotor = new Motor(22, 26, 24, 25);
		rightMotor = new Motor(23, 27, 28, 29);
	}

	@Override
	public void toggle(int pinNumber) {
		GpioPinDigitalOutput pin = leftMotor.getPin(pinNumber);

		if (pin == null) {
			pin = rightMotor.getPin(pinNumber);
		}

		if (pin != null) {
			pin.toggle();
		} else {
			System.out.println("Toggle failed, unable to find Pin " + pinNumber);
		}
	}

	@Override
	public void fd(int distance, int speed) throws InterruptedException {
		Sequence left = new ForwardSequence();
		Sequence right = new ForwardSequence();

		for (int x = 0; x < distance; x++) {
			left.step(leftMotor);
			right.step(rightMotor);
			Thread.sleep(speed);
		}

		leftMotor.low();
		rightMotor.low();
	}

	@Override
	public void rt(int degrees, int speed) throws InterruptedException {
		Sequence left = new ForwardSequence();
		Sequence right = new BackwardSequence();

		for (int x = 0; x < degrees; x++) {
			left.step(leftMotor);
			right.step(rightMotor);
			Thread.sleep(speed);
		}

		leftMotor.low();
		rightMotor.low();
	}

	@Override
	public void lt(int degrees, int speed) throws InterruptedException {
		Sequence left = new BackwardSequence();
		Sequence right = new ForwardSequence();

		for (int x = 0; x < degrees; x++) {
			left.step(leftMotor);
			right.step(rightMotor);
			Thread.sleep(speed);
		}

		leftMotor.low();
		rightMotor.low();
	}

	@Override
	public void execute(Sequence sequence, int delay) throws InterruptedException {
		// All sequences executed by this method must be non-repeating, to
		// prevent an infinite loop
		sequence.setRepeat(false);

		// Duplicate the sequence and run it on both motors
		Sequence sequence2 = sequence.clone();

		boolean running = true;

		while (running) {
			boolean leftSuccess = sequence.step(leftMotor);
			boolean rightSuccess = sequence2.step(rightMotor);

			Thread.sleep(delay);

			// If either motor fails to signal its pins, stop looping because
			// the sequence has finished
			if (!leftSuccess || !rightSuccess) {
				running = false;
			}
		}
	}
}