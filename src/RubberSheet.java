

import render.*;
import util.Effects;
import util.ForceImage;

import java.awt.*;
import java.util.ArrayList;

import com.tactonic.api.Tactonic;
import com.tactonic.api.TactonicDevice;
import com.tactonic.api.TactonicDeviceList;
import com.tactonic.api.TactonicFrame;
import com.tactonic.api.TactonicFrameEvent;
import com.tactonic.api.TactonicFrameListener;

public class RubberSheet extends RenderApplet implements TactonicFrameListener {
	Geometry sheet, floor;
	TactonicDevice device;
	TactonicFrame frame = null;

	Effects effects;

	int[] forceImage1; 
	int[] forceImage2;
	int p, xPrev = -1000, yPrev = 0;
	double buf[][][];
    int TW=32, TH=16;
	int N = 2; // NUMBER OF WAVE PROPAGATION ITERATIONS PER FRAME.
	int S = 4; // SCALE UP FACTOR FOR MESH RESOLUTION.
	int W = S*32;
	int H = S*32;
	double unitForce = -0.00002 / S / N;
	boolean start = false, isSpacePressed = false;
	int row0 = 0, col0 = 0, row1 = 0, col1 = 0, mouseX = -1, mouseY = -1;
	
	// Bouncing balls
	ArrayList<Ball> balls;
	
	// MIDI stuff
	MusicController music;
		
	public void initialize() {
		buf = new double[2][W][H];
		TactonicDeviceList deviceList = Tactonic.getDeviceList();
		if (deviceList.getNumDevices() > 0) {

			device = deviceList.getDevice(0);
			
			frame = new TactonicFrame(device);
			Tactonic.addFrameCallback(device, this);
			Tactonic.startDevice(device);
			
			TW = device.getCols();
			TH = device.getRows();
			System.out.println("Device detected: " + TW + "x" + TH);
		}
		else{
			System.out.println("Device Failed or No Device Connected");
		}
		
		forceImage1 = new int[TW * TH];
		forceImage2 = new int[TW * TH];	
		
		addLight(-1, -1, -1, 1, 1, 1);
		addLight( 1, -1,  1, 1, 1, 1);
		addLight(-1,  1, -1, 1, 1, 1);
		addLight( 1,  1,  1, 1, 1, 1);
		setFL(3.0);

		Material sheetMaterial = new Material();
		sheetMaterial.setAmbient(.0 * 0.5, .3 * 0.5, .4 * 0.5);
		sheetMaterial.setDiffuse(.0 * 1.8, .3 * 1.8, .4 * 1.8);
		sheetMaterial.setSpecular(1,1,1,10);
		sheetMaterial.setDoubleSided(true);

		Material floorMaterial = new Material();
		floorMaterial.setAmbient(0, 0, 1);
		floorMaterial.setDiffuse(0, 0, 1);
		floorMaterial.setTransparency(0.5);

		sheet = getWorld().add().mesh(W-1,H-1);
		sheet.setMaterial(sheetMaterial);

		floor = getWorld().add().cube();
		floor.setMaterial(floorMaterial);
		floor.getMatrix().translate(0, 0, -.05).scale(1, 1, .001);
		floor.isVisible = false;

		// init with random bouncing balls
		balls = new ArrayList<Ball>();
		for (int i=0; i<5; i++)
		{
			int ix = (int)(Math.random() * W);
			int iy = (int)(Math.random() * H);
			float x = (float)sheet.vertices[ix + iy*W][0]*20;
			float y = (float)sheet.vertices[ix + iy*W][1]*20;
			Ball b = new Ball(x, y, ix + iy*W);
			balls.add(b);
			getWorld().add(b);
		}
		
		// Music
		music = new MusicController(TW, TH);
		
		requestFocus();
	}
	
	public boolean mouseMove(Event e, int x, int y) {
		mouseX = x;
		mouseY = y;
		return true;
	}

	public boolean keyDown(Event e, int key) {
		switch (key) {
		case ' ':
			isSpacePressed = true;
			return true;
		}
		return false;
	}

	public boolean keyUp(Event e, int key) {
		if (key >= 'A' && key <= 'Z')
			key += 'a' - 'A';
		switch (key) {
		case '\n':
			unitForce = -unitForce;
			return true;
		case 'w':
			for (int i = 0; i < forceImage1.length; i++)
				System.out.print(forceImage2[i] + (i % TW == TW-1 ? "\n" : " "));
			return true;
		case 'f':
			floor.isVisible = !floor.isVisible;
			return true;
		case ' ':
			isSpacePressed = false;
			return true;
		}
		return false;
	}

    int bounce(int x, int W) {
        return x < 0 ? -x : x >= W ? 2*(W-1)-x : x;
    }
    double B(int p, int x, int y) {
        return buf[p][bounce(x,W)][bounce(y,H)];
    }
    void setB(int p, int x, int y, double value) {
        buf[p][bounce(x,W)][bounce(y,H)] = value;
    }
    
    int F(int x, int y) { 
//    	return y >= TH ? 0 : forceImage2[x + TW * y];
    	return forceImage2[x + TW * (y/2)];
    }
    double lerp(double t, double a, double b) { return a + t * (b - a); }
    
    double getForce(int x, int y) {
    	int x0 = x/S, x1 = Math.min(TW-1, x0+1);
    	int y0 = y/S, y1 = Math.min(TW-1, y0+1);
    	double u = (double)(x % S) / S;
    	double v = (double)(y % S) / S;
//    	System.out.println(x0 + "x" + y0 + " -> " + x1 + "x" + y1);
    	return (lerp(v, lerp(u, F(x0,y0), F(x1,y0)), lerp(u,F(x0,y1), F(x1,y1)))) * unitForce;
    }
    
 	public void animate(double time) {
 		
 		// IF NO SENSOR IS ATTACHED, USE MOUSE LOCATION INPUT WHILE SPACE KEY IS PRESSED.
 		
		if (frame == null) {
			col1 =        TW * mouseX / render.getWidth();
			row1 = TH-1 - TH * mouseY / render.getHeight();
			if (isSpacePressed) {
				int dCol = col1 - col0;
				int dRow = row1 - row0;
				int travel = Math.max(1, (int)Math.sqrt(dCol * dCol + dRow * dRow));
				for (int i = 1 ; i <= travel ; i++) {
					int col = col0 + dCol * i / travel;
					int row = row0 + dRow * i / travel;
					forceImage2[col + TW * row] = 5000 / N;
				}
			}
			row0 = row1;
			col0 = col1;
		}
		
		double dampen = 1.0 - (frame == null ? 0.1 : 0.04) / N;
		for (int n = 0 ; n < N ; n++) {
			for (int x = 0 ; x < W ; x++)
			for (int y = 0 ; y < H ; y++)
				setB(1-p,x,y, dampen * ( getForce(x, y) - B(1-p,x,y) +
						( B(p,x,y-1) + B(p,x-1,y) + B(p,x,y+1) + B(p,x+1,y) ) / 2 ));
			p = 1 - p;
		}
        
		for (int y = 0; y < H; y++)
		for (int x = 0; x < W; x++) 
			sheet.vertices[W * y + x][2] = B(p,x,y);
		
		sheet.computeSurfaceNormals();
		
		// bouncing balls
		for (Ball ball : balls) {
			ball.update(sheet);
		}
		
		// music
		music.processFrame(forceImage1);

		if (frame == null)
			for (int i = 0 ; i < forceImage2.length ; i++)
				forceImage2[i] /= 2;		
	}
 	
 	public void stop()
 	{
 		System.out.println("stop called");
 	}
	
	public void frameCallback(TactonicFrameEvent event) {
		forceImage2 = forceImage1.clone();
		forceImage1 = event.getFrame().getForces().clone();
	}
}
