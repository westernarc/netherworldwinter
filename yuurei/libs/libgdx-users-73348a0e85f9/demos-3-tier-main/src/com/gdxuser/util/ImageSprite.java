// wrapper for Image class that deals with loading an image

package com.gdxuser.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer10;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleTo;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.actors.Image;

public class ImageSprite extends Image {

	public float px, py;
	public float dx, dy, sx, sy;
	public float bx, by;
	public boolean paused = false;
	static final int CIRCLE_VERTICES = 10;
	float[] circleVerts = new float[2 * CIRCLE_VERTICES];
	ImmediateModeRenderer10 renderer = new ImmediateModeRenderer10();
	private Vector2 field;
	public float origScale = 1.0f;
	public boolean active = false;

	public float hiliteSize; // hilite box when picked
	public float hitDist;

	public boolean drawing = false;
	public boolean moving = false;
	public float vOffset;
	public float hOffset;

	public ImageSprite(String _name, String imgPath) {
		super(_name);

		if (imgPath != "none") {
			loadImage(imgPath);
			hiliteSize = width / 3;
			hitDist = width / 2;
			// setup offsets since origin has no effect
			vOffset = height / 2;
			hOffset = width / 2;
			centerOrigin();
		}
		touchable = true; // no effect tho...

		// pre calc
		// TODO - move to static - dont need this for every instance
		float angle = 0;
		float angleInc = 360.0f / CIRCLE_VERTICES;
		for (int i = 0, j = 0; i < CIRCLE_VERTICES; i++, angle += angleInc) {
			float x = (float) Math.cos(Math.toRadians(angle));
			float y = (float) Math.sin(Math.toRadians(angle));
			circleVerts[j++] = x;
			circleVerts[j++] = y;
		}

	}

	public void loadImage(String imgPath) {
		Texture texture = new Texture(Gdx.files.internal(imgPath));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		this.width = texture.getWidth();
		this.height = texture.getHeight();
		this.region = new TextureRegion(texture);
	}

	public void baseOrigin() {
		this.vOffset = 0f;
		this.hOffset = width / 2.0f;
		offsetOrigin();
	}

	public void centerOrigin() {
		this.vOffset = width / 2.0f;
		this.hOffset = width / 2.0f;
		offsetOrigin();
	}

	public void offsetOrigin() {
		this.originX = hOffset;
		this.originY = vOffset;
	}

	public ImageSprite setPos(float x, float y) {
		this.x = x; // odd you have to move the image and not the actor...
		this.y = y;
		px = x;
		py = y;
		return this; // allow chaining
	}

	public void randomPos(Vector2 _field) {
		field = _field;
		this.x = MathUtil.random(field.x);
		this.y = MathUtil.random(field.y);
	}

	// this is actually an override
	public boolean touched(float x, float y, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	public void bounce() {
		float spd = 0.5f;
		Action anim1 = ScaleTo.$(origScale * 2, origScale * 2, spd);
		Action anim2 = ScaleTo.$(origScale, origScale, spd);
		Sequence seq = Sequence.$(anim1, anim2);
		action(seq);
	}

	public void begin() {
		renderer.begin(GL10.GL_LINES);
	}

	public void drawLine(float x1, float y1, float x2, float y2, int r, int g,
			int b) {
		float fr = r / 255f;
		float fg = g / 255f;
		float fb = b / 255f;
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(x1, y1, 0);
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(x2, y2, 0);
	}

	public void fillCircle(float cx, float cy, float radius, int r, int g, int b) {
		end();
		renderer.begin(GL10.GL_TRIANGLE_FAN);
		float fr = r / 255f;
		float fg = g / 255f;
		float fb = b / 255f;
		int j = 0;
		for (int i = 0; i < CIRCLE_VERTICES - 1; i++) {
			renderer.color(fr, fg, fb, 1);
			renderer.vertex(cx + circleVerts[j] * radius, cy
					+ circleVerts[j + 1] * radius, 0);
			renderer.color(fr, fg, fb, 1);
			renderer.vertex(cx + circleVerts[j + 2] * radius, cy
					+ circleVerts[j + 3] * radius, 0);
			j += 2;
		}
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(cx + circleVerts[j] * radius, cy + circleVerts[j + 1]
				* radius, 0);
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(cx + circleVerts[0] * radius, cy + circleVerts[1]
				* radius, 0);
		end();
		begin();
	}

	public void frameCircle(float cx, float cy, float radius, float fr,
			float fg, float fb) {
		// float fr = r / 255f;
		// float fg = g / 255f;
		// float fb = b / 255f;

		int j = 0;
		// Util.log(String.format("rgb: %f %f %f", fr, fg, fb));
		for (int i = 0; i < CIRCLE_VERTICES - 1; i++) {
			renderer.color(fr, fg, fb, 1);
			renderer.vertex(cx + circleVerts[j] * radius, cy
					+ circleVerts[j + 1] * radius, 0);
			renderer.color(fr, fg, fb, 1);
			renderer.vertex(cx + circleVerts[j + 2] * radius, cy
					+ circleVerts[j + 3] * radius, 0);
			j += 2;
		}
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(cx + circleVerts[j] * radius, cy + circleVerts[j + 1]
				* radius, 0);
		renderer.color(fr, fg, fb, 1);
		renderer.vertex(cx + circleVerts[0] * radius, cy + circleVerts[1]
				* radius, 0);
	}

	public void drawRect(float x, float y, float w, float h, Vector3 col) {
		renderer.color(col.x, col.y, col.z, 1);
		renderer.vertex(x, y, 0);
		renderer.color(col.x, col.y, col.z, 1);
		renderer.vertex(x + w, y, 0);
		renderer.color(col.x, col.y, col.z, 1);
		renderer.vertex(x + w, y + h, 0);
		renderer.color(col.x, col.y, col.z, 1);
		renderer.vertex(x, y + h, 0);
		renderer.color(col.x, col.y, col.z, 1);
		renderer.vertex(x, y, 0);
		renderer.color(col.x, col.y, col.z, 1);
	}

	public void setColor(boolean flag) {
		if (flag) {
			color.set(1f, 1f, 1f, 0.8f); // clear
		} else {
			color.set(1f, 1f, 1f, 1f); // clear
		}
	}

	public void end() {
		renderer.end();
	}

	public boolean checkHit(Actor other) {
		if (!moving) {
			return false;
		} // only collide once
		Vector2 myPos = new Vector2(x, y);
		Vector2 otherPos = new Vector2(other.x, other.y);
		if (myPos.dst(otherPos) < hitDist) {
			return true;
		}
		return false;
	}

}
