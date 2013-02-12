package com.wiztoybox.examplelauncher.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.wiztoybox.examplelauncher.Example;

public class ColoredSquares extends Example {

    private OrthographicCamera camera;

    private Mesh greenSquare;
    private Mesh redSquare;
    private Mesh blueSquare;
    private Mesh yellowSquare;
    
    private float green = Color.toFloatBits(0, 255, 0, 255);
    private float red = Color.toFloatBits(255, 0, 0, 255);
    private float blue = Color.toFloatBits(0, 0, 255, 255);
    private float yellow = Color.toFloatBits(255, 255, 0, 255);
    
    private float elapsedTime = 0;
    private float interval = 1;     // one second

	@Override
    public void create() {
        if (greenSquare == null) {
            greenSquare = createSquareMesh(-0.5f, -0.5f, 0.5f, green);
        }
        if (redSquare == null) {
            redSquare = createSquareMesh(0, -0.5f, 0.5f, red);
        }
        if (blueSquare == null) {
            blueSquare = createSquareMesh(-0.5f, 0, 0.5f, blue);
        }
        if (yellowSquare == null) {
            yellowSquare = createSquareMesh(0, 0, 0.5f, yellow);
        }
    }

	@Override
	public void resize(int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera = new OrthographicCamera(2 * aspectRatio, 2);
	}

    @Override
    public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		update(deltaTime);
		draw(deltaTime);
    }

	private void update(float deltaTime) {
        camera.update();
        camera.apply(Gdx.gl10);

        elapsedTime += Gdx.graphics.getDeltaTime();
	}

	private void draw(float deltaTime) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);

        if (elapsedTime > interval)
        	blueSquare.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
        if (elapsedTime > 2 * interval)
        	yellowSquare.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
        if (elapsedTime > 3 * interval)
        	greenSquare.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
        if (elapsedTime > 4 * interval)
        	redSquare.render(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

    private Mesh createSquareMesh(float x, float y, float size, float color) {
    	// (x, y) is at the bottom, left corner; size is the size of a square
    	
        Mesh mesh = new Mesh(true, 4, 4, 
                new VertexAttribute(Usage.Position, 3, "a_position"),
                new VertexAttribute(Usage.ColorPacked, 4, "a_color"));

        float x2 = x + size;
        float y2 = y + size;
        mesh.setVertices(new float[] {
                x,  y,  0, color,
                x2, y,  0, color,
                x,  y2, 0, color,
                x2, y2, 0, color });  
        
        mesh.setIndices(new short[] { 0, 1, 2, 3});
        
        return mesh;
    }

	@Override
	public boolean needsGL20() {
		// TODO Auto-generated method stub
		return false;
	}

}
