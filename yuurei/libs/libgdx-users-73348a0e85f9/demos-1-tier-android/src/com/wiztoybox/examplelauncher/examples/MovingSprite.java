package com.wiztoybox.examplelauncher.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class MovingSprite extends Sprite {

	private int boundWidth, boundHeight;
	private float dirX, dirY;

	public MovingSprite(Texture texture) {
		super(texture);
		
		boundWidth = Gdx.graphics.getWidth() - this.getRegionWidth();
		boundHeight = Gdx.graphics.getHeight() - this.getRegionHeight();
		dirX = dirY = 0;
	}
	
	public void setBounds(int width, int height) {
		boundWidth = width - this.getRegionWidth();
		boundHeight = height - this.getRegionHeight();
	}
	
	public void setSpeed(float dirX, float dirY) {
		this.dirX = dirX;
		this.dirY = dirY;
	}
	
	public void update(float deltaTime) {
		this.translate(dirX * deltaTime, dirY * deltaTime);
		float newX = this.getX();
		float newY = this.getY();
		if (newX < 0) {
			dirX = -dirX;
			newX = 0;
		}
		if (newX > boundWidth) {
			dirX = -dirX;
			newX = boundWidth;
		}
		if (newY < 0) {
			dirY = -dirY;
			newY = 0;
		}
		if (newY > boundHeight) {
			dirY = -dirY;
			newY = boundHeight;
		}
		this.setPosition(newX, newY);
	}
}
