import java.util.HashMap;

public class MusicController {
	final int THRESHOLD = 500;
	final int MAX_FORCE = 4000;
	int sensorWidth, sensorHeight;
	
	// notesMap is a map between the forces frame and notes and channels
	NoteBox[] notesMap;
	
	MidiSender midiSender;
	
	public MusicController(int w, int h)
	{
		sensorWidth = w;
		sensorHeight = h;
		
		midiSender = new MidiSender();
		
		notesMap = new NoteBox[w*h];
		for (int i=0; i<w*h; i++)
		{
			notesMap[i] = null;
		}

		// define a square of note triggers
//		int[] scale = {60, 62, 64, 65, 67, 69, 71, 72, 74};	// C Major
		int[] scale = {60, 62, 64, 67, 69};	// Pentatonic
		createInstrument(0, 0, 2, 3, 10, 1, scale, 0);
		createInstrument(0, 10, 2, 3, 10, 1, scale, 1);
	}
	
	/* createSquareInstrument
	 * 
	 * x, y = start position
	 * sbx, sby = size of each note box
	 * nbx, nby = number of note boxes per x and y 
	 */
	public void createInstrument(int _x, int _y, int sbx, int sby, int nbx, int nby, int[] scale, int channel)
	{
		int w = sbx*nbx;
		int h = sby*nby;
		int scaleIndex = 0;
		int octave = 0;
		for (int y=_y; y<_y+h; y+=sby) {
			for (int x=_x; x<_x+w; x+=sbx) {
				NoteBox nb = new NoteBox(channel, scale[scaleIndex++] + octave*12);
				if (scaleIndex >= scale.length) {
					scaleIndex=0;
					octave++;
				}
				
				for (int j=0; j<sby; j++) {
					for (int i=0; i<sbx; i++) {
						notesMap[(x+i) + (y+j)*sensorWidth] = nb;
					}
				}
			}
		}
		
	}
	
	public void processFrame(int[] forces)
	{
		// reset down state before starting
		for (NoteBox nb : notesMap) {
			if (nb != null) {
				nb.isDown = false;
			}
		}

		for (int i=0; i<forces.length; i++)
		{
			if (notesMap[i] != null) {
				NoteBox nb = notesMap[i];
				if (forces[i] > THRESHOLD) {
					nb.isDown = true;
					if (!nb.isPlaying) {
						nb.isPlaying = true;
						midiSender.sendNoteOn(nb.channel, nb.pitch, (int)map(forces[i], THRESHOLD, MAX_FORCE, 0, 127));
					}
				}
			}
		}
		
		// release notes
		for (NoteBox nb : notesMap) {
			if (nb != null)
			{
				if (nb.isPlaying && !nb.isDown)
				{
					midiSender.sendNoteOff(nb.channel, nb.pitch);
					nb.isPlaying = false;									
				}
			}
		}		
	}
	
	
	private float map(float val, float smin, float smax, float tmin, float tmax)
	{
		return tmin + (tmax - tmin) * ((val - smin) / (smax - smin));
	}

	public class NoteBox
	{
		boolean active;
		int channel;
		int pitch;
		boolean isDown;
		boolean isPlaying;
		
		
		public NoteBox()
		{
			active = false;
		}
		
		public NoteBox(int c, int p)
		{
			active = true;
			channel = c;
			pitch = p;
			isPlaying = false;
			isDown = false;
		}
	}
}


