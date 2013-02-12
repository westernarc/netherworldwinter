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
/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.wiztoybox.examplelauncher;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.wiztoybox.examplelauncher.examples.*;

public abstract class Example extends AndroidApplication implements ApplicationListener {

	// Class files must reside in this path
	private static final String examplePath = "com.wiztoybox.examplelauncher.examples";

	//TODO: It appears getting examples automatically can't be done with reflection... see
	//TODO: http://stackoverflow.com/questions/1456930/read-all-classes-from-java-package-in-classpath 
	public static final Demo[] examples = {
		// add new Examples here in array examples.
		// Parameters: Title, class [, orientation]
		// Class files must reside in the given examplePath above!
		new Demo("Colored Background", ColoredBackground.class),
		new Demo("Colored Lines", ColoredLines.class),
		new Demo("Colored Squares", ColoredSquares.class,
				ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE),
		new Demo("Moving Sprites", MovingSprites.class) };	
	
	//TODO: Titles and orientation setting must be set somehow or
	//TODO: should be read out with getTitle and getOrientation methods
	//TODO: in all tests (add to this class as abstract method stubs??)

	public static String[] getNames () {
		List<String> names = new ArrayList<String>();
		for (Demo exampleClass : examples){
			names.add(exampleClass.getClassname().getSimpleName());
			}
		return names.toArray(new String[names.size()]);
	}

	public static Example newExample(String exampleName)
	{
		try {
			Class<?> exampleClass = Class.forName(examplePath + "." + exampleName);
			return (Example)exampleClass.newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}	
	
	public abstract boolean needsGL20 ();

	public void create () {
	};

	public void resume () {
	};

	public void render () {
	};

	public void resize (int width, int height) {
	};

	public void pause () {
	};

	public void dispose () {
	};
}