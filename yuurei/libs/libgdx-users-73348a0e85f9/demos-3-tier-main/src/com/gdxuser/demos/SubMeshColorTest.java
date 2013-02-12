package com.gdxuser.demos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.gdxuser.util.DemoWrapper;

public class SubMeshColorTest extends DemoWrapper{
	GL10 gl;
	PerspectiveCamera cam;
	ObjLoader model = new ObjLoader();
	StillModel plane;

	@Override
	public void create() {
		gl = Gdx.app.getGraphics().getGL10();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);

		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);

		// Enable Z-sorting, depth test
		gl.glEnable(GL10.GL_DEPTH_TEST);

		// Enable per vertex / per face color
		// activates "Color Tracking" which tells OpenGL that it
		// will get material attributes from glColor calls
		// glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
		// rather than this long form calls:
		// glMaterialfv(GL_FRONT, GL_AMBIENT_AND_DIFFUSE, float[4]);
//		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		
		// Position the camera
		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		cam.translate(0, 0, 4f);
		cam.near = 1f;
		cam.far = 100;
		cam.update();
		cam.apply(gl);
		
		
		// loading the model from file
		plane = model.loadObj(Gdx.files.internal("data/3d/plane2_parts.obj"));
	}
	@Override
	public void render() {
		// clear screen and set it to white
		// never forget |GL10.GL_DEPTH_BUFFER_BIT
		// because this makes 3D-Meshes flicker
		// and they get some kind of shine-through
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glPushMatrix();

		// just some rotation to see parts of the model better...
		gl.glRotatef(45, 1, 0, 0);
		gl.glRotatef(45, 0, 1, 0);

		// rendering model's submeshes (their IDs are given
		// in OBJ file, see lines starting with "o"...)
		// with different solid colors applied on each
		gl.glColor4f(1, 0, 0, 1);
		plane.getSubMesh("plane_body").mesh.render(GL10.GL_TRIANGLES);		
		gl.glColor4f(0, 1, 0, 1);
		plane.getSubMesh("plane_wings").mesh.render(GL10.GL_TRIANGLES);		
		gl.glColor4f(0, 0, 1, 1);
		plane.getSubMesh("plane_rudder").mesh.render(GL10.GL_TRIANGLES);
		gl.glPopMatrix();
	}
}
