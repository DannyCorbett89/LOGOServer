package com.dc.logoserver.robot.pinstates;

import com.dc.logoserver.robot.Motor;
import com.pi4j.io.gpio.PinState;

/**
 * Represents a set of four pin states which can be applied to a {@link Motor}
 */
public class Signal {
	protected PinState[] states;

	public Signal(PinState p1, PinState p2, PinState p3, PinState p4) {
		states = new PinState[4];
		states[0] = p1;
		states[1] = p2;
		states[2] = p3;
		states[3] = p4;
	}

	/**
	 * Gets an ordered array of {@link PinState}s
	 * 
	 * @return The array of {@link PinState}
	 */
	public PinState[] getStates() {
		return states;
	}
}
