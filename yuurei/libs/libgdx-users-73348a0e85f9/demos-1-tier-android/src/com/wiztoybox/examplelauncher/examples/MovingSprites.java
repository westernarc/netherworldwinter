package com.wiztoybox.examplelauncher.examples;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.wiztoybox.examplelauncher.Example;

public class MovingSprites extends Example {

	private static final int SPRITES = 400;
    
    private SpriteBatch batch;
    private Texture texture;
    private Random random = new Random();
    private MovingSprite[] sprites = new MovingSprite[SPRITES];
    private	BitmapFont font;
    private Vector2 textPos;

    private int fps = 60;
    private float maxSprites = 0;
    private int rate = 30;  // sprites per sec
    private float timePassed = 0;

	@Override
	public void create() {
		texture = new Texture(Gdx.files.internal("images/babymonkey.png"));
		
		// Store random sprites in the array
        for (int i = 0; i < SPRITES; ++i) {
            MovingSprite sprite = new MovingSprite(texture);
		    int randX = random.nextInt(Gdx.graphics.getWidth() - texture.getWidth());
		    int randY = random.nextInt(Gdx.graphics.getHeight() - texture.getHeight() - 40);		    
            sprite.setPosition(randX, randY);
            sprite.setBounds(Gdx.graphics.getWidth(),
            		Gdx.graphics.getHeight()- 40);
            sprite.setSpeed(50, 50);
            sprites[i] = sprite;
        }
        
        // Set up default font
		font = new BitmapFont();
		font.setColor(Color.GREEN);
		textPos = new Vector2(20, Gdx.graphics.getHeight() - 10);
        
		batch = new SpriteBatch();
	}

	@Override
	public void render() {		
		float deltaTime = Gdx.graphics.getDeltaTime();		
		update(deltaTime);		
        draw(deltaTime);
    }

	private void update(float deltaTime) {
		fps = Gdx.graphics.getFramesPerSecond();
		timePassed += deltaTime;
		if (fps > 30 && timePassed > 2) {
    		if (maxSprites < SPRITES) {
    			maxSprites += rate * deltaTime;
    			if (maxSprites > SPRITES)
    				maxSprites = SPRITES;
    		}
		}
		
		for(int i = 0; i < maxSprites; i++) {
			sprites[i].update(deltaTime);
		}    		
	}

	private void draw(float deltaTime) {		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
    	Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
        for(int i = 0; i < maxSprites; i++) {
            sprites[i].draw(batch);
        }
		font.draw(batch, "Sprites: " + (int)maxSprites  + "    FPS: " + fps, 
				(int)textPos.x, (int)textPos.y);
		batch.end();
	}

	@Override
	public boolean needsGL20() {
		// TODO Auto-generated method stub
		return false;
	}

}
