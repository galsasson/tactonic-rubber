package com.tactonic.api;

public class TactonicDeviceEvent {
	protected TactonicDevice device;
	protected int eventType;
	protected int eventCode;
	
	TactonicDeviceEvent(TactonicDevice device){
		this.device = device;
	}
	
	public TactonicDevice getDevice(){
		return device;
	}

	public int getEventType(){
		return eventType;
	}

	public int getEventCode(){
		return eventCode;
	}
	
}
