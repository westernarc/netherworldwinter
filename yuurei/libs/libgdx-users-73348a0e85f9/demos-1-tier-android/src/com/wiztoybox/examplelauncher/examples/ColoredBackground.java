package com.wiztoybox.examplelauncher.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.wiztoybox.examplelauncher.Example;

//public class ColoredBackground extends AndroidApplication implements ApplicationListener{
	public class ColoredBackground extends Example{

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	// background color
	private float red = 1;
	private float green = 0;
	private float blue = 0;
	
	private int state = 1;
	private float rate = 2;

	@Override
    public void create() {
    }

    @Override
    public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
		update(deltaTime);
		draw(deltaTime);
    }

	private void update(float deltaTime) {
		float grad = rate * deltaTime;
		
		switch (state) {
		case 1:
			green += grad;
			if (green > 1) {
				green = 1;
				state = 2;
			}
			break;
		case 2:
			red -= grad;
			if (red < 0) {
				red = 0;
				state = 3;
			}
			break;
		case 3:
			blue += grad;
			if (blue > 1) {
				blue = 1;
				state = 4;
			}
			break;
		case 4:
			green -= grad;
			if (green < 0) {
				green = 0;
				state = 5;
			}
			break;
		case 5:
			red += grad;
			if (red > 1) {
				red = 1;
				state = 6;
			}
			break;
		case 6:
			blue -= grad;
			if (blue < 0) {
				blue = 0;
				state = 1;  // re-cycle
			}
		}
	}

	private void draw(float deltaTime) {
        Gdx.gl.glClearColor(red, green, blue, 1);
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public boolean needsGL20() {
		// TODO Auto-generated method stub
		return false;
	}

}
