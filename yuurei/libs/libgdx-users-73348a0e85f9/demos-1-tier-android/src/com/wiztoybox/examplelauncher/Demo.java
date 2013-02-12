package com.wiztoybox.examplelauncher;

import android.content.pm.ActivityInfo;

/* Data structure for examples list */
public class Demo
{
	public String title;
	public Class<?> classname;
	public int orientation;

	public Demo(String title, Class<?> classname)
	{
		this.title = title;
		this.classname = classname;
		// adds default orientation ...
		this.orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
	}

	// classname has to end with ".class"
	// orientation can be ORIENTATION_PORTRAIT
	// or ORIENTATION_LANDSCAPE or ORIENTATION_SQUARE
	public Demo(String title, Class<?> classname, int orientation)
	{
		this.title = title;
		this.classname = classname;
		this.orientation = orientation;
	}

	public String getTitle() {
		return title;
	}

	public Class<?> getClassname() {
		return classname;
	}

	public int getOrientation() {
		return orientation;
	}
}	