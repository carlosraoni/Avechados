package com.car.ai;

import java.util.BitSet;

import com.car.model.Car;

public interface CarIntelligenceInterface {

	public abstract BitSet getCarNextControls(Car opponent);

}