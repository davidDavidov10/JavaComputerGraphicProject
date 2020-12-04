package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;
import com.jogamp.opengl.*;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;

import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;
/**
 * A F1 Racing Car.
 *
 */
public class F1Car implements IRenderable, IIntersectable {
	Center carCenter = new Center();
	Back carBack = new Back();
	Front carFront = new Front();

	@Override
	public void render(GL2 gl) {
		carCenter.render(gl);
		gl.glPushMatrix();
		gl.glTranslated(-Specification.B_LENGTH / 2.0 - Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carBack.render(gl);
		gl.glPopMatrix();
		gl.glPushMatrix();
		gl.glTranslated(Specification.F_LENGTH / 2.0 + Specification.C_BASE_LENGTH / 2.0, 0.0, 0.0);
		carFront.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public String toString() {
		return "F1Car";
	}

	@Override
	public void init(GL2 gl) {

	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		// Return a list of bounding spheres the list structure is as follow:
		// s1 -> s2 -> s3 -> s4
		// where:
		// s1 - sphere bounding the whole car
		// s2 - sphere bounding the car front
		// s3 - sphere bounding the car center
		// s4 - sphere bounding the car back
		//
		// * NOTE:
		// All spheres should be adapted so that they are place relative to
		// the car model coordinate system.
		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();	
	      double r1 = Math.pow(Specification.B_LENGTH + (Specification.C_LENGTH/2), 2) + Math.pow(Specification.B_HEIGHT/2, 2) + Math.pow(Specification.B_DEPTH/2, 2);
	      r1 = Math.sqrt(r1);
	      double r2 = Math.pow(Specification.F_LENGTH + (Specification.C_LENGTH/2), 2) + Math.pow(Specification.F_DEPTH, 2); 
	      r2 = Math.sqrt(r2);

	      Point p = new Point(0, Specification.B_HEIGHT/2, 0);
	      	      
	      double maxradius = Math.max(r1, r2);
	      res.add(new BoundingSphere(maxradius, p));
	        
	
	      for(BoundingSphere sphere : carFront.getBoundingSpheres()) {
	    	  sphere.translateCenter(Specification.F_LENGTH / 2.0 + Specification.C_BASE_LENGTH / 2.0, 0, 0);
	    	  res.add(sphere);
	      }
	      res.addAll(carCenter.getBoundingSpheres());
	      
	      for(BoundingSphere sphere : carBack.getBoundingSpheres()) {
	    	  sphere.translateCenter(-Specification.B_LENGTH / 2 - Specification.C_BASE_LENGTH / 2, 0, 0);
	    	  res.add(sphere);
	      }
	      return res;
	  }

	@Override
	public void destroy(GL2 gl) {
		// TODO Auto-generated method stub
		
	}	
}
