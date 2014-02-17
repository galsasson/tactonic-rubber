package com.tactonic.api;

public class Tactonic {

	public static TactonicDeviceList getDeviceList() {
		TactonicJNI.getDeviceList(TactonicJNI.deviceList);
		return TactonicJNI.deviceList;
	}

	public static void pollFrame(TactonicDevice device, TactonicFrame frame) {
		TactonicJNI.pollFrame(device, frame);
	}

	public static void startDevice(TactonicDevice device) {
		TactonicJNI.startDevice(device);
	}

	public static void stopDevice(TactonicDevice device) {
		TactonicJNI.stopDevice(device);
	}
	
	public static void addFrameCallback(TactonicDevice device, TactonicFrameListener listener) {
		TactonicJNI.addFrameCallback(device, listener);
		TactonicJNI.frameListener.add(listener);
		TactonicJNI.frameEvent.add(new TactonicFrameEvent(device));
	}

	public static void removeFrameCallback(TactonicDevice device, TactonicFrameListener listener) {
		int index = TactonicJNI.frameListener.indexOf(listener);
		if(index >= 0){
			TactonicJNI.removeFrameCallback(device, listener);
			TactonicJNI.frameListener.remove(index);
			TactonicJNI.frameEvent.remove(index);
		}
	}

	public static void addDeviceCallback(TactonicDevice device, TactonicDeviceListener listener) {
		TactonicJNI.addDeviceCallback(device, listener);
		TactonicJNI.deviceListener.add(listener);
		TactonicJNI.deviceEvent.add(new TactonicDeviceEvent(device));
	}

	public static void removeDeiceCallback(TactonicDevice device, TactonicDeviceListener listener) {
		int index = TactonicJNI.deviceListener.indexOf(listener);
		if(index >= 0){
			TactonicJNI.removeDeviceCallback(device, listener);
			TactonicJNI.deviceListener.remove(index);
			TactonicJNI.deviceEvent.remove(index);
		}
	}

	public static void copyFrame(TactonicFrame src, TactonicFrame dst) {
		if(src.image.length == dst.image.length){
			dst.image = src.image.clone();
			dst.frameNumber = src.frameNumber;
			dst.time = src.time;
			dst.rows = src.rows;
			dst.cols = src.cols;
		}
	}

}