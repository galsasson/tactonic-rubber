import javax.sound.midi.*;

public class MidiSender {
	MidiDevice device;
	Receiver recv;
	
	public MidiSender() {
		MidiDevice.Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
		
		try {
			for (int i=0; i<midiInfo.length; i++)
			{
				MidiDevice dev = MidiSystem.getMidiDevice(midiInfo[i]);
				if (dev.getMaxReceivers() != 0)
				{
					System.out.println("Found Midi Receiver["+i+"]: " + midiInfo[i].getName() + ", " + midiInfo[i].getDescription());
					if (midiInfo[i].getName().equals("IAC Bus 1")) {
						device = dev;
						recv = dev.getReceiver();
						device.open();
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("error while getting midi device info");
		}		
	}
	
	public void sendNoteOn(int channel, int pitch, int vel)
	{
		try
		{
			ShortMessage msg = new ShortMessage();
			msg.setMessage(ShortMessage.NOTE_ON, channel, pitch, vel);
			recv.send(msg, -1);
		}
		catch (Exception e) {
			System.out.println("error sending midi message: ");
			System.out.println(e.toString());
		}
	}
	
	public void sendNoteOff(int channel, int pitch)
	{
		try
		{
			ShortMessage msg = new ShortMessage();
			msg.setMessage(ShortMessage.NOTE_OFF, channel, pitch, 0);
			recv.send(msg, -1);
		}
		catch (Exception e) {
			System.out.println("error sending midi message");
			System.out.println(e.toString());
		}		
	}
	
	
}
