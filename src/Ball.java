import render.*;

import java.util.Date;

public class Ball extends Geometry {

    private static final double GRAVITY = -0.0002,
                                V_INIT = -0.8,
                                Z_INIT = 7.0,
                                FLOOR_Z_POS = 0.11,
                                FLOOR_DAMPENING = 0.9;
    private static final int ANIM_TIME = 550;

    private double velocity = 0;
    private boolean animate = false;
    private long startTime;

    public Ball() {
		Material ballMaterial = new Material();
		ballMaterial.setAmbient(0.04, 0.02, 0.02);
		ballMaterial.setDiffuse(0.02, 0.01, 0.01);
		ballMaterial.setSpecular(0.8, 0.4, 0.4, 10);
		ballMaterial.setDoubleSided(true);

        sphere(10);
        setMaterial(ballMaterial);

        isVisible = false;
    }

    public void update() {
        if (animate) {
            if (getMatrix().get(2, 3) < FLOOR_Z_POS) {
                getMatrix().set(2, 3, FLOOR_Z_POS);
                velocity = -velocity * FLOOR_DAMPENING;    
            }

            long timeSinceStart = new Date().getTime() - startTime;
            velocity += (GRAVITY * timeSinceStart);
            getMatrix().translate(0, 0, velocity);

            if (timeSinceStart >= ANIM_TIME) {
                reset();
            }
        }
    }

    public void setPosition(double x, double y) {
        getMatrix().identity();
        getMatrix().scale(0.1).translate(x, y, Z_INIT);
    }

    public void startAnimation(double x, double y) {
        setPosition(x, y);
        startAnimation();
    }

    public void startAnimation() {
        isVisible = true;
        velocity = V_INIT;
        animate = true; 
        startTime = new Date().getTime();
    }

    public void reset() {
        isVisible = false;
        velocity = 0;
        animate = false;
    }
}
