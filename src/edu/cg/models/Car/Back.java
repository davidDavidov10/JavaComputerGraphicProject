package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class Back implements IRenderable, IIntersectable {
	private SkewedBox baseBox = new SkewedBox(Specification.B_BASE_LENGTH, Specification.B_BASE_HEIGHT,
			Specification.B_BASE_HEIGHT, Specification.B_BASE_DEPTH, Specification.B_BASE_DEPTH);
	private SkewedBox backBox = new SkewedBox(Specification.B_LENGTH, Specification.B_HEIGHT_1,
			Specification.B_HEIGHT_2, Specification.B_DEPTH_1, Specification.B_DEPTH_2);
	private PairOfWheels wheels = new PairOfWheels();
	private Spolier spoiler = new Spolier();
	
	
	
	private SkewedBox exhaust_pipe_one = new SkewedBox(0.1, 0.02, 0.02, 0.02,0.02);
	   private SkewedBox exhaust_pipe_two = new SkewedBox(0.15/2, 0.02, 0.02, 0.02,0.02);
	@Override
	public void render(GL2 gl) {
		gl.glPushMatrix();
		Materials.SetBlackMetalMaterial(gl);
		gl.glTranslated(Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0, 0.0, 0.0);
		baseBox.render(gl);
		Materials.SetRedMetalMaterial(gl);
		gl.glTranslated(-1.0 * (Specification.B_LENGTH / 2.0 - Specification.B_BASE_LENGTH / 2.0),
				Specification.B_BASE_HEIGHT, 0.0);
		backBox.render(gl);
		render_exhaust(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 + Specification.TIRE_RADIUS, 0.5 * Specification.TIRE_RADIUS,
				0.0);
		wheels.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 + 0.5 * Specification.S_LENGTH,
				0.5 * (Specification.B_HEIGHT_1 + Specification.B_HEIGHT_2), 0.0);
		spoiler.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		// Return a list of bounding spheres the list structure is as follow:
		// s1
		// where:
		// s1 - sphere bounding the car front
		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();
			
		double r = Math.pow(Specification.B_LENGTH/2, 2) + Math.pow(Specification.B_HEIGHT/2, 2) + Math.pow(Specification.B_DEPTH/2, 2);
	      r = Math.sqrt(r);
	      Point p = new Point(0, Specification.B_HEIGHT/2, 0);
	      BoundingSphere sphere = new BoundingSphere(r, p);
	      sphere.setSphereColore3d(0, 0, 1);
	      res.add(sphere);
	      return res;
	}
	//*****************************************************
	// we add a new exhaust feature to the car
	//*****************************************************
	public void render_exhaust(GL2 gl) {
		   GLU glu = new GLU();
		   GLUquadric quad = glu.gluNewQuadric();
		   
		   gl.glPushMatrix();
		   //left  exhaust
		   gl.glTranslated(-0.3, 0, 0);
		   Materials.SetDarkGreyMetalMaterial(gl);
		   this.exhaust_pipe_one.render(gl);
		  
		   //left exhaust opening
		   gl.glTranslated(-0.045, 0.0095, 0D);
		   Materials.SetBlackMetalMaterial(gl);
		   glu.gluSphere(quad, 0.01, 10, 10);
		   		   
		   //right  exhaust
		   gl.glTranslated(0.045, -0.0095, 0.03D);
		   Materials.SetDarkGreyMetalMaterial(gl);
		   this.exhaust_pipe_two.render(gl);

		   //right exhaust opening
		   gl.glTranslated(-0.032, 0.0095, 0D);
		   Materials.SetBlackMetalMaterial(gl);
		   glu.gluSphere(quad, 0.01, 10, 10);		   
		   gl.glPopMatrix(); 
	   }

	@Override
	public void destroy(GL2 gl) {
		// TODO Auto-generated method stub
		
	}
}
