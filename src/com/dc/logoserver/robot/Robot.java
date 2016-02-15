package com.dc.logoserver.robot;

import com.dc.logoserver.robot.pinstates.Sequence;

public interface Robot {
	/**
	 * Toggles a specific pin on or off
	 * 
	 * @param pinNumber
	 *            Number of the pin to be toggled
	 */
	public void toggle(int pinNumber);

	/**
	 * Moves the robot forwards
	 * 
	 * @param distance
	 *            Amount to move forward by
	 * @param speed
	 *            Number of milliseconds to wait in between each step. The
	 *            higher the number, the slower the robot will move
	 * @throws InterruptedException
	 */
	public void fd(int distance, int speed) throws InterruptedException;

	/**
	 * Turns the robot right
	 * 
	 * @param degrees
	 *            Angle in degrees for the robot to turn
	 * @param speed
	 *            Number of milliseconds to wait in between each step. The
	 *            higher the number, the slower the robot will move
	 * @throws InterruptedException
	 */
	public void rt(int degrees, int speed) throws InterruptedException;

	/**
	 * Turns the robot left
	 * 
	 * @param degrees
	 *            Angle in degrees for the robot to turn
	 * @param speed
	 *            Number of milliseconds to wait in between each step. The
	 *            higher the number, the slower the robot will move
	 * @throws InterruptedException
	 */
	public void lt(int degrees, int speed) throws InterruptedException;

	/**
	 * Executes a {@link Sequence} of steps
	 * 
	 * @param sequence
	 *            {@link Sequence} of steps to be executed
	 * @param delay
	 *            How long to wait in between each step of the sequence
	 */
	public void execute(Sequence sequence, int delay) throws InterruptedException;
}