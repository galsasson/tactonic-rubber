package com.tactonic.api;

import java.util.ArrayList;

class TactonicJNI {

	static TactonicDeviceList deviceList = new TactonicDeviceList();
	static ArrayList<TactonicFrameListener> frameListener = new ArrayList<TactonicFrameListener>();
	static ArrayList<TactonicFrameEvent> frameEvent = new ArrayList<TactonicFrameEvent>();
	static ArrayList<TactonicDeviceListener> deviceListener = new ArrayList<TactonicDeviceListener>();
	static ArrayList<TactonicDeviceEvent> deviceEvent = new ArrayList<TactonicDeviceEvent>();
	
	native static void getDeviceList(TactonicDeviceList deviceList);

	native static void startDevice(TactonicDevice device);

	native static void stopDevice(TactonicDevice device);

	native static void pollFrame(TactonicDevice device, TactonicFrame frame);
	
	native static void addFrameCallback(TactonicDevice device, TactonicFrameListener listener);
	
	native static void removeFrameCallback(TactonicDevice device, TactonicFrameListener listener);
	
	native static void addDeviceCallback(TactonicDevice device, TactonicDeviceListener listener);
	
	native static void removeDeviceCallback(TactonicDevice device, TactonicDeviceListener listener);

	static void frameCallback(int serialNumber){
		for(int i = 0; i < frameEvent.size(); i++){
			if(frameEvent.get(i).getDevice().getSerialNumber() == serialNumber){
				pollFrame(frameEvent.get(i).getDevice(),frameEvent.get(i).frame);
				frameListener.get(i).frameCallback(frameEvent.get(i));
			}
		}
	}

	static void deviceCallback(int serialNumber){
		for(int i = 0; i < deviceEvent.size(); i++){
			if(deviceEvent.get(i).getDevice().getSerialNumber() == serialNumber){
				deviceListener.get(i).deviceCallback(deviceEvent.get(i));
			}
		}
	}
	
	static {
		try {
			System.loadLibrary("TactonicJNI");
			System.out.println("Tactonic Library Loaded");
		} catch (Exception e) {
			System.out.println("Tactonic Library Not Loaded");
		}
	}
}