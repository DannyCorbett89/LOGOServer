package com.dc.logoserver.robot.pinstates;

import java.util.ArrayList;
import java.util.List;

import com.pi4j.io.gpio.PinState;

/**
 * Sequence which is run on startup. It turns each pin on, then each pin off, to
 * test that they are all working correctly
 */
public class StartupSequence extends Sequence {
	@Override
	public List<Signal> initializeSteps() {
		List<Signal> forward = new ArrayList<Signal>();
		forward.add(new Signal(PinState.LOW, PinState.LOW, PinState.LOW, PinState.HIGH));
		forward.add(new Signal(PinState.LOW, PinState.LOW, PinState.HIGH, PinState.HIGH));
		forward.add(new Signal(PinState.LOW, PinState.HIGH, PinState.HIGH, PinState.HIGH));
		forward.add(new Signal(PinState.HIGH, PinState.HIGH, PinState.HIGH, PinState.HIGH));
		forward.add(new Signal(PinState.HIGH, PinState.HIGH, PinState.HIGH, PinState.LOW));
		forward.add(new Signal(PinState.HIGH, PinState.HIGH, PinState.LOW, PinState.LOW));
		forward.add(new Signal(PinState.HIGH, PinState.LOW, PinState.LOW, PinState.LOW));
		forward.add(new Signal(PinState.LOW, PinState.LOW, PinState.LOW, PinState.LOW));

		return forward;
	}
}