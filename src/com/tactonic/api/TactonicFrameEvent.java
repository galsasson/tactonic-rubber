package com.tactonic.api;

public class TactonicFrameEvent {
	protected TactonicDevice device;
	protected TactonicFrame frame;
	
	TactonicFrameEvent(TactonicDevice device){
		this.device = device;
		this.frame = new TactonicFrame(device);
	}
	
	public TactonicDevice getDevice(){
		return device;
	}
	
	public TactonicFrame getFrame(){
		return frame;
	}
}
