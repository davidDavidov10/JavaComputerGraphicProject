package edu.cg.models.Car;

import java.util.LinkedList;
import java.util.List;
import com.jogamp.opengl.GL2;
import edu.cg.algebra.Point;
import edu.cg.models.BoundingSphere;
import edu.cg.models.IIntersectable;
import edu.cg.models.IRenderable;

public class Front implements IRenderable, IIntersectable {
	private FrontBumber frontBumpber = new FrontBumber();
	private FrontHood hood = new FrontHood();
	private PairOfWheels wheels = new PairOfWheels();

	@Override
	public void render(GL2 gl) {
		//  Render the BUMPER.
		gl.glPushMatrix();
		// Render hood - Use Red Material.
		gl.glTranslated(-Specification.F_LENGTH / 2.0 + Specification.F_HOOD_LENGTH / 2.0, 0.0, 0.0);
		hood.render(gl);
		// Render the wheels.
		gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0 - 1.25 * Specification.TIRE_RADIUS,0.5 * Specification.TIRE_RADIUS, 0);				
		wheels.render(gl);
		gl.glTranslated(Specification.F_HOOD_LENGTH / 2.0 - 1.25 * Specification.TIRE_RADIUS + 0.03, -0.5 * Specification.TIRE_RADIUS, 0);
		frontBumpber.render(gl);
		gl.glPopMatrix();
	}

	@Override
	public void init(GL2 gl) {
	}

	@Override
	public List<BoundingSphere> getBoundingSpheres() {
		//  Return a list of bounding spheres the list structure is as follow:
		// s1
		// where:
		// s1 - sphere bounding the car front
		LinkedList<BoundingSphere> res = new LinkedList<BoundingSphere>();

		double radius = Math.pow((Specification.F_LENGTH/2), 2) + Math.pow(Specification.F_HEIGHT/2, 2) + Math.pow(Specification.C_DEPTH/2, 2);
	    radius = Math.sqrt(radius);	      
        Point center = new Point(0, Specification.F_HEIGHT/2, 0);
        BoundingSphere boundingSphere = new BoundingSphere(radius, center);
        boundingSphere.setSphereColore3d(1, 0, 0);
        res.add(boundingSphere);
      return res;
	}

	@Override
	public String toString() {
		return "CarFront";
	}




	@Override
	public void destroy(GL2 gl) {
		// TODO Auto-generated method stub
		
	}
}
