package com.westernarc.yuurei;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplet;

public class YuureiApplet extends LwjglApplet {
    private static final long serialVersionUID = 1L;
    public YuureiApplet()
    {
        super(new Yuurei(), false);
    }
}
