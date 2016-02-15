package com.dc.logoserver.robot.pinstates;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.PinState;

/**
 * Sequence to be run when responding to a ping. Turns all pins on, then turns
 * all pins off
 */
public class PingSequence extends Sequence {
	@Override
	public List<Signal> initializeSteps() {
		List<Signal> forward = new ArrayList<Signal>();
		forward.add(new Signal(PinState.HIGH, PinState.HIGH, PinState.HIGH, PinState.HIGH));
		forward.add(new Signal(PinState.LOW, PinState.LOW, PinState.LOW, PinState.LOW));

		return forward;
	}
}
