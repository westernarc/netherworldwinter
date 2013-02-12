package com.westernarc.util;

import java.io.PrintStream;

import com.westernarc.util.FastMath;

public class FastTrigTest {
	public static void main(String[] args) {
		PrintStream out = System.out;
		out.println(FastMath.STEP);
		out.println("Precision: " + FastMath.PRECISION);
		
		float timer = System.nanoTime();
		
		for(int q = 0; q < 1000; q++) {
			for(int i = 0; i < FastMath.PRECISION; i++) {
				FastMath.sin(i);
				FastMath.cos(i);
			}
		}
		float t1 =  System.nanoTime() - timer;
		timer = System.nanoTime();
		
		for(int q = 0; q < 1000; q++) {
			for(int i = 0; i < FastMath.PRECISION; i++) {
				FastMath.sin(i);
				FastMath.cos(i);
			}
		}
		float t2 = System.nanoTime() - timer;
		System.out.println("Calc time: " + t2 + " ;; Access time: " + t1);
	}
}
