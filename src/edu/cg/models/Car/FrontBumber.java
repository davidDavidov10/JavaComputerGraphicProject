package edu.cg.models.Car;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import edu.cg.models.IRenderable;
import edu.cg.models.SkewedBox;

public class FrontBumber implements IRenderable {
   private SkewedBox bumperBox = new SkewedBox(Specification.F_BUMPER_LENGTH, Specification.F_BUMPER_HEIGHT_1, Specification.F_BUMPER_HEIGHT_2, 
		   Specification.F_BUMPER_DEPTH, Specification.F_BUMPER_DEPTH);
   private SkewedBox bumperWings = new SkewedBox(Specification.S_LENGTH, Specification.F_BUMPER_WINGS_HEIGHT_1, Specification.F_BUMPER_WINGS_HEIGHT_2, Specification.F_BUMPER_WINGS_DEPTH, Specification.F_BUMPER_WINGS_DEPTH);
   
   private SkewedBox BlackBumber = new SkewedBox(Specification.F_BUMPER_LENGTH, Specification.F_BUMPER_HEIGHT_1 + 0.0001 , Specification.F_BUMPER_HEIGHT_2+0.0001 , 
		   Specification.F_BUMPER_DEPTH/2.5, Specification.F_BUMPER_DEPTH/2.5);
   	
   private SkewedBox redBumber = new SkewedBox(Specification.F_BUMPER_LENGTH, Specification.F_BUMPER_HEIGHT_1 + 0.0003 , Specification.F_BUMPER_HEIGHT_2+0.0003 , 
		   Specification.F_BUMPER_DEPTH/5, Specification.F_BUMPER_DEPTH/5);
  
  
   
   public void render(GL2 gl) {
	  
      GLU glu = new GLU();
      GLUquadric q = glu.gluNewQuadric();
      gl.glPushMatrix();
      Materials.SetRedMetalMaterial(gl);
      bumperBox.render(gl);
      
      Materials.SetBlackMetalMaterial(gl);
      BlackBumber.render(gl);
      
      Materials.SetRedMetalMaterial(gl);
      redBumber.render(gl);
      
      gl.glTranslated(0, 0, Specification.F_BUMPER_DEPTH/2 + 0.04); 
      renderBumperWing(gl, glu, q);
      gl.glTranslated(0, 0,  2*(-Specification.F_BUMPER_DEPTH/2-0.04));
      renderBumperWing(gl, glu, q);
      gl.glPopMatrix();
      glu.gluDeleteQuadric(q);
   }
   
   private void renderBumperWing(GL2 gl, GLU glu, GLUquadric q) {
	      gl.glPushMatrix();
	      Materials.SetBlackMetalMaterial(gl);
	      this.bumperWings.render(gl);
	      renderFlashLights(gl, glu, q);	      
	      gl.glPopMatrix();
   }
   
   private void renderFlashLights(GL2 gl, GLU glu, GLUquadric q) {
	  double radius = 0.03;
   	  gl.glPushMatrix();
      gl.glTranslated(0, (Specification.F_BUMPER_WINGS_HEIGHT_1 / 2)-(0.33 * radius), 0);
      glu.gluSphere(q, radius, 10, 10);
      gl.glPopMatrix();
   }

   public void init(GL2 gl) {
   }

   public String toString() {
      return "FrontBumper";
   }

@Override
public void destroy(GL2 gl) {
	// TODO Auto-generated method stub
	
}
}
