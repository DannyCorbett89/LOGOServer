package com.dc.logoserver.robot.pinstates;

import java.util.List;

import com.dc.logoserver.robot.Motor;

/**
 * Represents a set of steps that will be executed in sequence. When the last
 * step has been executed, it will loop back to the first step
 */
public abstract class Sequence {
	protected List<Signal> steps;
	protected int current;
	protected boolean repeat;

	public Sequence() {
		steps = initializeSteps();
		repeat = true;
	}

	public abstract List<Signal> initializeSteps();

	/**
	 * Sets whether or not this sequence should repeat when it reaches the end
	 * 
	 * @param repeat
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * Executes a single step of the sequence and then prepares the next step to
	 * be run. If this sequence is non-repeating and all steps have been
	 * executed, this method will do nothing
	 * 
	 * @return true if the GPIOs were signalled, otherwise false
	 */
	public boolean step(Motor motor) {
		if (current >= steps.size()) {
			if (repeat) {
				current = 0;
			} else {
				System.out.println("End of sequence reached");
				return false;
			}
		}

		Signal signal = steps.get(current);

		if (!motor.execute(signal)) {
			return false;
		}

		current++;

		return true;
	}

	/**
	 * Creates an identical copy of the {@link Sequence}
	 * 
	 * @return A new instance of an identical {@link Sequence}
	 */
	public Sequence clone() {
		Sequence sequence = null;

		try {
			sequence = getClass().newInstance();
			sequence.setRepeat(repeat);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return sequence;
	}
}
