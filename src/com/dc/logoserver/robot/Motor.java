package com.dc.logoserver.robot;

import com.dc.logoserver.robot.pinstates.Sequence;
import com.dc.logoserver.robot.pinstates.Signal;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Represents a Motor which is controlled by four GPIO pins
 */
public class Motor {
	protected static final String GPIO_PREFIX = "GPIO ";

	protected GpioPinDigitalOutput[] pins;
	protected Sequence sequence;

	/**
	 * Creates a new {@link Motor}, with the four specified GPIO pins
	 * 
	 * @param p1
	 *            A {@link GpioPinDigitalOutput}'s number
	 * @param p2
	 *            A {@link GpioPinDigitalOutput}'s number
	 * @param p3
	 *            A {@link GpioPinDigitalOutput}'s number
	 * @param p4
	 *            A {@link GpioPinDigitalOutput}'s number
	 */
	public Motor(int p1, int p2, int p3, int p4) {
		GpioController gpio = GpioFactory.getInstance();
		pins = new GpioPinDigitalOutput[4];
		pins[0] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PREFIX + p1));
		pins[1] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PREFIX + p2));
		pins[2] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PREFIX + p3));
		pins[3] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PREFIX + p4));
	}

	/**
	 * Gets a specific pin. If none of the pins match the given number, null is
	 * returned
	 * 
	 * @param pinNumber
	 *            GPIO number of the pin to be returned
	 * @return The correct {@link GpioPinDigitalOutput}, otherwise null
	 */
	public GpioPinDigitalOutput getPin(int pinNumber) {
		for (GpioPinDigitalOutput pin : pins) {
			if (pin.getName().equals(GPIO_PREFIX + pinNumber)) {
				return pin;
			}
		}

		return null;
	}

	/**
	 * Sends a signal to the GPIO pins
	 * 
	 * @param signal
	 *            Set of four {@link PinState}s to be applied to the GPIO pins
	 * @return
	 */
	public boolean execute(Signal signal) {
		PinState[] states = signal.getStates();

		if (pins.length != states.length) {
			System.out.println("Number of pins does not match number of states. Pins: " + pins.length + ", States: "
					+ states.length);
			return false;
		}

		for (int x = 0; x < states.length; x++) {
			pins[x].setState(states[x]);
		}

		return true;
	}

	/**
	 * Turns off all GPIO pins
	 */
	public void low() {
		for (int x = 0; x < pins.length; x++) {
			pins[x].low();
		}
	}
}
