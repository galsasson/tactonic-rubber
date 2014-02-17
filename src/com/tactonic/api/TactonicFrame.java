package com.tactonic.api;

public class TactonicFrame {

	protected int[] image;
	protected long frameNumber;
	protected double time;
	protected int rows;
	protected int cols;

	public TactonicFrame(TactonicDevice device) {
		image = new int[device.rows * device.cols];
		rows = device.rows;
		cols = device.cols;
	}

	public int[] getForces() {
		return image;
	}

	public double getTimeStamp() {
		return time;
	}

	public long getFrameNumber() {
		return frameNumber;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
}
