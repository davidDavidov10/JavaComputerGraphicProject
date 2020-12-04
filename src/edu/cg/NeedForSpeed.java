package edu.cg;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;
import edu.cg.models.BoundingSphere;
import edu.cg.models.Track;
import edu.cg.models.TrackSegment;
import edu.cg.models.Car.F1Car;
import edu.cg.models.Car.Specification;

/**
 * An OpenGL 3D Game.
 *
 */
public class NeedForSpeed implements GLEventListener {
	private GameState gameState = null; // Tracks the car movement and orientation
	private F1Car car = null; // The F1 car we want to render
	private Vec carCameraTranslation = null; // The accumulated translation that should be applied on the car, camera
												// and light sources
	private Track gameTrack = null; // The game track we want to render
	private FPSAnimator ani; // This object is responsible to redraw the model with a constant FPS
	private Component glPanel; // The canvas we draw on.
	private boolean isModelInitialized = false; // Whether model.init() was called.
	private boolean isDayMode = true; // Indicates whether the lighting mode is day/night.
	private boolean isBirdseyeView = false; // Indicates whether the camera is looking from above on the scene or
											// looking towards the car direction.
	private double[] carInitialPosition;
    private double[] cameraInitialPositionBirdsView;
    private double[] cameraInitialPositionThirdpersonView;
    private int carScale =4;
    private float alpha;
    private float beta;
	
	public NeedForSpeed(Component glPanel) {
		this.carInitialPosition = new double[]{0, this.carScale * Specification.TIRE_RADIUS,-1*( carScale * Specification.CAR_LENGTH)-2 };
	   	this.cameraInitialPositionBirdsView = new double[]{this.carInitialPosition[0], 50, this.carInitialPosition[2] - this.carScale * Specification.CAR_LENGTH - 22};
        this.cameraInitialPositionThirdpersonView = new double[]{this.carInitialPosition[0], 2, this.carInitialPosition[2] + 4 + this.carScale * Specification.CAR_LENGTH};
       
        this.alpha = (float)this.carScale * (float)Specification.C_LENGTH / 2 + (float)this.carScale * (float)Specification.F_LENGTH;
        this.beta = (float)this.carScale *(float)Specification.F_BUMPER_DEPTH/2;
        
        this.glPanel = glPanel;
		gameState = new GameState();
		gameTrack = new Track();
		carCameraTranslation = new Vec(0.0);
		car = new F1Car();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
	      if (!this.isModelInitialized) {
	         this.initModel(gl);
	      }

	      if (this.isDayMode) {
	         gl.glClearColor(0.2F, 0.5F, 1F, 1F);
	      } else {
	         gl.glClearColor(0.1F, 0.1F, 0.2F, 1F);
	      }

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// This is the flow in which we render the scene.
		// Step (1) Update the accumulated translation that needs to be
		// applied on the car, camera and light sources.
		updateCarCameraTranslation(gl);
		// Step (2) Position the camera and setup its orientation
		setupCamera(gl);
		// Step (3) setup the lights.
		setupLights(gl);
		// Step (4) render the car.
		renderCar(gl);
		// Step (5) render the track.
		renderTrack(gl);
		// Step (6) check collision. Note this has nothing to do with OpenGL.
		if (checkCollision()) {
			JOptionPane.showMessageDialog(this.glPanel, "Game is Over");
			this.gameState.resetGameState();
			this.carCameraTranslation = new Vec(0.0);
		}

	}

	/**
	 * @return Checks if the car intersects the one of the boxes on the track.
	 */
	private boolean checkCollision() {
		 List<BoundingSphere> carSpheres = car.getBoundingSpheres();
	     List<BoundingSphere> trackBoundingSpheres = gameTrack.getBoundingSpheres();
	     
	      for(BoundingSphere sphere : carSpheres) {
	    	  sphere.setRadius(carScale * sphere.getRadius());
	    	  rotateAboutYAxis(90 + gameState.getCarRotation(),sphere);
	    	  sphere.translateCenter(carInitialPosition[0] + (double)carCameraTranslation.x, carInitialPosition[1] + (double)carCameraTranslation.y, carInitialPosition[2] + (double)carCameraTranslation.z);
	      }
	      
	      if (trackBoundingSpheres.isEmpty()) {
              return false;
          }
	      
	      for(BoundingSphere boxSphere : trackBoundingSpheres) {
	    	  if(boxSphere.checkIntersection(carSpheres.get(0))) {	    		  
	    		  for(int i = 1; i < carSpheres.size(); i++) {	    			  
	    			  if(boxSphere.checkIntersection(carSpheres.get(i))){
	    				  return true;
	    			  }
	    		  }	    		  
	    	  }
	      }
	      return false;
	}
	
	
	private void rotateAboutYAxis(double degree, BoundingSphere sphere) {
		  double sinTheta = Math.sin(Math.toRadians(degree));
	      double cosTheta = Math.cos(Math.toRadians(degree));
	      double newX = cosTheta * sphere.getCenter().x + sinTheta * sphere.getCenter().z;
	      double newZ = -sinTheta * sphere.getCenter().x + cosTheta * sphere.getCenter().z;
	      sphere.setCenter(new Point(newX, sphere.getCenter().y, newZ)); 
	   }
	
	private void updateCarCameraTranslation(GL2 gl) {
		// Update the car and camera translation values (not the ModelView-Matrix).
		// - Always keep track of the car offset relative to the starting
		// point.
		// - Change the track segments here.
		Vec ret = gameState.getNextTranslation();
		carCameraTranslation = carCameraTranslation.add(ret);
		double dx = Math.max(carCameraTranslation.x, -TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 - 2);
		carCameraTranslation.x = (float) Math.min(dx, TrackSegment.ASPHALT_TEXTURE_DEPTH / 2.0 + 2);
		if (Math.abs(carCameraTranslation.z) >= TrackSegment.TRACK_LENGTH + 10.0) {
			carCameraTranslation.z = -(float) (Math.abs(carCameraTranslation.z) % TrackSegment.TRACK_LENGTH);
			gameTrack.changeTrack(gl);
		}
	}

	private void setupCamera(GL2 gl) {
		GLU glu = new GLU();
	      double eyeX;
	      double eyeY;
	      double eyeZ;
	      if (this.isBirdseyeView) {
	         eyeX = cameraInitialPositionBirdsView[0] + (double)carCameraTranslation.x;
	         eyeY = cameraInitialPositionBirdsView[1] + (double)carCameraTranslation.y;
	         eyeZ = cameraInitialPositionBirdsView[2] + (double)carCameraTranslation.z;
	         glu.gluLookAt(eyeX, eyeY, eyeZ, eyeX, eyeY - 1, eyeZ, 0, 0, -1);
	      } else {
	         eyeX =cameraInitialPositionThirdpersonView[0] + (double)carCameraTranslation.x;
	         eyeY =cameraInitialPositionThirdpersonView[1] + (double)carCameraTranslation.y;
	         eyeZ =cameraInitialPositionThirdpersonView[2] + (double)carCameraTranslation.z;
	         glu.gluLookAt(eyeX, eyeY, eyeZ, eyeX, eyeY, eyeZ-1 , 0, 1, 0);	    	  
	      }
	}

	private void setupLights(GL2 gl) {
		if (this.isDayMode) {
	        gl.glDisable(GL2.GL_LIGHT1);
			float[] sunIntensity = new float[]{1, 1, 1, 1};
		      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, sunIntensity, 0);
		      gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, sunIntensity, 0);
		      float[] sunDirection = new float[]{0, 1, 1};
		      gl.glLightfv(GL2.GL_LIGHT0,GL2.GL_POSITION ,sunDirection , 0);
		      gl.glEnable(GL2.GL_LIGHT0);
	      } 
		else {
		      gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, new float[]{0.25f, 0.25f, 0.3f, 1}, 0);
	         double carAngle = -Math.toRadians(this.gameState.getCarRotation());
	         float cosTheta = (float)Math.cos(carAngle);
	         float sinTheta = (float)Math.sin(carAngle);
	         
	         float[] firstPosition = new float[]{
	     carCameraTranslation.x + (float)carInitialPosition[0] -alpha * sinTheta + beta * cosTheta,
	        		 
	        		 
	     (float)carInitialPosition[1] + (float)(carScale * Specification.F_BUMPER_HEIGHT_1/2.0),
	        		 
	     carCameraTranslation.z + (float)carInitialPosition[2] -alpha * cosTheta - beta * sinTheta, 1};
	         	         
	         float[] directionLight = new float[]{-sinTheta, 0, -cosTheta};
	         setCarFlashLight(gl, GL2.GL_LIGHT0, firstPosition, directionLight);
	         
	         
	         float[] secondPosition = new float[]{carCameraTranslation.x + 
	        		 (float)carInitialPosition[0] - alpha * sinTheta - beta *cosTheta,
	        		 
	        		 (float)carInitialPosition[1] + (float)(carScale * Specification.F_BUMPER_HEIGHT_1/2.0),
	        		 
	        		 carCameraTranslation.z + (float)carInitialPosition[2] - alpha * cosTheta + beta * sinTheta, 1};	      	         
	         setCarFlashLight(gl, GL2.GL_LIGHT1, secondPosition, directionLight);
	      }
	}
	
	private void setCarFlashLight(GL2 gl, int light, float[] pos, float[] direction) {
	      float[] sunColor = new float[]{0.85f, 0.85f, 0.85f, 1};
	      gl.glLightfv(light, GL2.GL_POSITION, pos, 0);
	      gl.glLightf(light, GL2.GL_SPOT_CUTOFF, 90);
	      gl.glLightfv(light, GL2.GL_SPOT_DIRECTION, direction, 0);
	      gl.glLightfv(light, GL2.GL_SPECULAR, sunColor, 0);
	      gl.glLightfv(light, GL2.GL_DIFFUSE, sunColor, 0);
	      gl.glEnable(light);
	   }
	
	
	private void renderTrack(GL2 gl) {
		// * Note: the track is not translated. It should be fixed.
		gl.glPushMatrix();
		gameTrack.render(gl);
		gl.glPopMatrix();
	}

	private void renderCar(GL2 gl) {
		//  Render the car.
		// * Remember: the car position should be the initial position + the accumulated translation.
		//             This will simulate the car movement.
		// * Remember: the car was modeled locally, you may need to rotate/scale and translate the car appropriately.
		// * Recommendation: it is recommended to define fields (such as car initial position) that can be used during rendering.
	      gl.glPushMatrix();
	      gl.glTranslated(carInitialPosition[0] + (double)carCameraTranslation.x, carInitialPosition[1] + (double)carCameraTranslation.y,carInitialPosition[2] + (double)carCameraTranslation.z);
	      gl.glRotated(90 - gameState.getCarRotation(), 0, 1, 0);
	      gl.glScaled(carScale, carScale,carScale);
	      car.render(gl);
	      gl.glPopMatrix();
	}

	public GameState getGameState() {
		return gameState;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Initialize display callback timer
		ani = new FPSAnimator(30, true);
		ani.add(drawable);
		glPanel.repaint();

		initModel(gl);
		ani.start();
	}

	public void initModel(GL2 gl) {
		gl.glCullFace(GL2.GL_BACK);
		gl.glEnable(GL2.GL_CULL_FACE);

		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_SMOOTH);

		car.init(gl);
		gameTrack.init(gl);
		isModelInitialized = true;
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		  GL2 gl = drawable.getGL().getGL2();
	      GLU glu = new GLU();
	      double aspectRatio = (double)width / height;
	      gl.glMatrixMode(GL2.GL_PROJECTION);
	      gl.glLoadIdentity();
	      glu.gluPerspective(60, aspectRatio, 2, 500);
	}

	/**
	 * Start redrawing the scene with 30 FPS
	 */
	public void startAnimation() {
		if (!ani.isAnimating())
			ani.start();
	}

	/**
	 * Stop redrawing the scene with 30 FPS
	 */
	public void stopAnimation() {
		if (ani.isAnimating())
			ani.stop();
	}

	public void toggleNightMode() {
		isDayMode = !isDayMode;
	}

	public void changeViewMode() {
		isBirdseyeView = !isBirdseyeView;
	}

}
