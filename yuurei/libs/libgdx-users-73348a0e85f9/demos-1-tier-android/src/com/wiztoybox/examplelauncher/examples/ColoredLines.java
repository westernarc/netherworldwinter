package com.wiztoybox.examplelauncher.examples;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.wiztoybox.examplelauncher.Example;

public class ColoredLines extends Example {

    private static final int LINE_COUNT = 120;
    
    private Random random = new Random();
    private RandomLine[] randLines = new RandomLine[LINE_COUNT];

    private Texture texture;
    private TextureRegion region;
    private SpriteBatch batch;
    
    private int maxLines = 0;

	@Override
	public void create() {
		
        // Store random lines in an asrray
        for (int i = 0; i < LINE_COUNT; i++) {
        	randLines[i] = new RandomLine();    // see the class below
        }
        
        // Create a texture to contain the pixmap
        texture = new Texture (2048, 2048, Pixmap.Format.RGBA8888); 
        texture.setFilter(Texture.TextureFilter.Nearest, 
                Texture.TextureFilter.Linear);
        texture.setWrap(Texture.TextureWrap.ClampToEdge, 
                Texture.TextureWrap.ClampToEdge);
              
        batch = new SpriteBatch();
	}

    @Override
    public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		update(deltaTime);
		draw(deltaTime);
    }

	private void update(float deltaTime) {
        if (maxLines < LINE_COUNT) { 
        	maxLines++;
        }
        region = this.drawLinesOnRegion(maxLines);
	}

	private void draw(float deltaTime) {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
        batch.begin();
        batch.draw(region, 0, 0);
        batch.end();
	}

	private TextureRegion drawLinesOnRegion(int maxLines) {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		// Create a drawable pixmap 
        Pixmap pixmap = new Pixmap(width, height,
                Pixmap.Format.RGBA8888);
        
        for (int i = 0; i < maxLines; i++) {
        	RandomLine randLine = randLines[i];
        	pixmap.setColor(randLine.red, randLine.green, randLine.blue, 1);
        	pixmap.drawLine(randLine.x1, randLine.y1, randLine.x2, randLine.y2);
        }
                
        // Blit the composited overlay to a texture
        texture.draw(pixmap, 0, 0);
        pixmap.dispose();
        
        return new TextureRegion(texture, 0, 0, width,height);
	}

    private class RandomLine 
    {
        public int x1 = random.nextInt(Gdx.graphics.getWidth());
        public int y1 = random.nextInt(Gdx.graphics.getHeight());
        public int x2 = random.nextInt(Gdx.graphics.getWidth());
        public int y2 = random.nextInt(Gdx.graphics.getHeight());

        public float red = random.nextFloat();
        public float green = random.nextFloat();
        public float blue = random.nextFloat();
    }

	@Override
	public boolean needsGL20() {
		// TODO Auto-generated method stub
		return false;
	}

}
