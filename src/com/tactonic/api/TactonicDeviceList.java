package com.tactonic.api;

public class TactonicDeviceList {
	protected TactonicDevice[] devices = new TactonicDevice[32];
	protected int numDevices;
	
	public int getNumDevices() {
		return numDevices;
	}
	
	public TactonicDevice getDevice(int i){
		if(i < numDevices && i >= 0)
			return devices[i];
		else
			return null;
	}
	
	protected void setDevice(int i, int serial, int rows, int cols){
		devices[i] = new TactonicDevice();
		devices[i].serialNumber = serial;
		devices[i].rows = rows;
		devices[i].cols = cols;
	}
}
