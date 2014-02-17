import render.*;

import java.util.Date;

public class Ball extends Geometry {

    private static final float GRAVITY = -0.02f,
                                Z_INIT = 15.0f,
                                SCALE = 0.05f;

    private float vel = 0;
    private float acc = GRAVITY;
    int vertexIndex;
    

    public Ball(float x, float y, int vi) {
    	vertexIndex = vi;
		Material ballMaterial = new Material();
		ballMaterial.setAmbient(0.04, 0.02, 0.02);
		ballMaterial.setDiffuse(0.02, 0.01, 0.01);
		ballMaterial.setSpecular(0.8, 0.4, 0.4, 10);
		ballMaterial.setDoubleSided(true);

        sphere(10);
        getMatrix().scale(SCALE).translate(x, y, Z_INIT);
        setMaterial(ballMaterial);
    }

    public void update(Geometry sheet) {
    	double z = sheet.vertices[vertexIndex][2] + SCALE;
        if (getMatrix().get(2, 3) < z) {
        	getMatrix().set(2, 3, z);
            vel = -vel;
        }
		
        vel += acc;
        getMatrix().translate(0, 0, vel);
        
        vel *= 0.95f;
    }

//    public void setPosition(double x, double y) {
//        getMatrix().identity();
//        getMatrix().scale(0.1).translate(x, y, Z_INIT);
//    }

//    public void startAnimation(double x, double y) {
//        setPosition(x, y);
//        startAnimation();
//    }

//    public void startAnimation() {
//        isVisible = true;
//        velocity = V_INIT;
//        animate = true; 
//        startTime = new Date().getTime();
//    }

//    public void reset() {
//        isVisible = false;
//        velocity = 0;
//        animate = false;
//    }
}
