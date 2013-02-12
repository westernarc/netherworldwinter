package com.westernarc.util;

//Look up table for trig values.  Creates table upon loading class
public class FastMath {
	public static final float FOURPI = 12.56637f;
	//Number of values to calculate the angle for, between 0 and TWOPI
	public static int PRECISION = 512;
	public static float TWOPI = 6.283185f;
	
	//Space between each angle value
	public static float STEP = TWOPI/PRECISION;
	
	public static float PI = 3.141592f;
	public static float HALFPI = 1.570796f;
	
	//Look up tables for Sin and Cos
	static float[] SIN_TABLE;
	static float[] COS_TABLE;
	
	static {
		SIN_TABLE = new float[PRECISION];
		COS_TABLE = new float[PRECISION];
		for(int i = 0; i < PRECISION; i++) {
			SIN_TABLE[i] = (float)Math.sin(((float)i/PRECISION) * TWOPI);
			COS_TABLE[i] = (float)Math.cos(((float)i/PRECISION) * TWOPI);
		}
	}
	
	public static float sin(float a) {
		while(a < 0) a += TWOPI;
		//Wrap around TWOPI
		a = a % TWOPI;
		a = (a / TWOPI) * ( PRECISION );
		return SIN_TABLE[(int)a];	
	}
	
	public static float cos(float b) {
		while(b < 0) b += TWOPI; 
		b = b % TWOPI;
		b = (b / TWOPI) * ( PRECISION );
		return COS_TABLE[(int)b];	
	}
}
