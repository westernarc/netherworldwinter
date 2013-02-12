/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

// very simple test to show a color screen + change color when clicked

package com.gdxuser.demos;

import com.gdxuser.util.DemoWrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL10;


public class SimpleTest extends DemoWrapper implements InputProcessor {
	float r = 1, g = 0, b = 0;

	@Override public void create () {
		Gdx.app.log("Simple Test", "Thread=" + Thread.currentThread().getId() + ", surface created");
		Gdx.input.setInputProcessor(this);
	}

	@Override public void render () {
		GL10 gl = Gdx.app.getGraphics().getGL10();

		gl.glClearColor(r, g, b, 1);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	@Override public void dispose () {
		Gdx.app.log("Simple Test", "Thread=" + Thread.currentThread().getId() + ", application destroyed");
	}

	@Override public boolean touchUp (int x, int y, int pointer, int button) {
		r = (float)Math.random();
		g = (float)Math.random();
		b = (float)Math.random();
		return false;
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
