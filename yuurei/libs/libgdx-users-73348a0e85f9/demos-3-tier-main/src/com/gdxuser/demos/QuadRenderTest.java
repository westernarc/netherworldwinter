package com.gdxuser.demos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.gdxuser.util.DemoWrapper;

/* @author: E.Spitz
 * @date: August, 19th 2011
 */
public class QuadRenderTest extends DemoWrapper implements Screen {

	OrthographicCamera camera;
	Mesh quad;
	Texture quadTexture;
	
	@Override
	public void show() {

		// Set up a camera that will contain our mesh in it's frustum
		// Camera looks at [0,0] by default, so I'll place the mesh there
		camera = new OrthographicCamera(20, 20);
		
		// We wont need indices if we use GL_TRIANGLE_FAN to draw our quad
		// TRIANGLE_FAN will draw the verts in this order: 0, 1, 2; 0, 2, 3
		quad = new Mesh(true, 4, 0, 
				new VertexAttribute(Usage.Position, 3, "a_position"), 
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
		// Set our verts up in a CCW order
		quad.setVertices(new float[] {
				-5f, -5f, 0, 0, 1,	// bottom left
				5f, -5f, 0, 1, 1,	// bottom right
				5f, 5f, 0, 1, 0,	// top right
				-5f, 5f, 0, 0, 0});	// top left
		
		// Load up a texture to apply to our mesh...
		quadTexture = new Texture(Gdx.files.internal("data/img/planet_earth.png"));
		
		// Since the camera will never move in this example, 
		// I'll just update and apply the matrices right here
		// Normally this would be done in the render call, however.
		camera.update();
		camera.apply(Gdx.gl10);
		
	}
	
	@Override
	public void render(float delta) {
		GL10 gl = Gdx.gl10;
		// Clear the color buffer
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		// Enable texturing
		gl.glEnable(GL10.GL_TEXTURE_2D);
			
		// Bind our texture so it will be applied to the quad
		quadTexture.bind();
		
		// Let's save the matrix state, so we can put it back at
		// the end of the render.. remember that glXXX calls are
		// cumulative - even through render calls
		gl.glPushMatrix();
		
		// Move the first quad to the upper left corner
		gl.glTranslatef(-5f, 5f, 0);
		
		// Apply a red tint to it!
		gl.glColor4f(0.5f, 0, 0, 1);
		
		// Render the quad with TRIANGLE_FAN
		quad.render(GL10.GL_TRIANGLE_FAN);
		
		// Now let's move the quad to the lower right and tint
		// it blue... keep mind of the fact that glTranslatef
		// calls are cumulative
		gl.glTranslatef(10f, -10f, 0);
		gl.glColor4f(0, 0, 0.5f, 1);
		quad.render(GL10.GL_TRIANGLE_FAN);
		
		// How about we render one in the top right corner in green!
		gl.glTranslatef(0, 10f, 0);
		gl.glColor4f(0, 0.5f, 0, 1);
		quad.render(GL10.GL_TRIANGLE_FAN);
		
		// And last but not least, let's render one in the lower
		// left corner using it's original (texture) color
		gl.glTranslatef(-10f, -10f, 0);
		gl.glColor4f(1, 1, 1, 1);
		quad.render(GL10.GL_TRIANGLE_FAN);
		
		// Restore the matrix to it's original state
		gl.glPopMatrix();
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}