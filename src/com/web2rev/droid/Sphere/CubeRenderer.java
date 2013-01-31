package com.web2rev.droid.Sphere;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLSurfaceView;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;
public class CubeRenderer implements Renderer {
    public static final float INIT_DSCALE=0.38f;
    public float scale=1;
    public float dScale=0.38f;
    public float ddScale=0.05f;
    public float xangle=0;
    public float dxangle=1f;
    public float yangle=0;
    public float dyangle=1f;
    public float zangle=0;
    public float dzangle=1f;
    private Square square;
    private boolean mTranslucentBackground;
    private Thread gyroscopeThread;
    private boolean gyroscopeRunning=false;
    private Object gyroscopeMutex=new Object();
    public CubeRenderer()
    {
	this.square = new Square();
    }
    private Context context;
    public CubeRenderer(boolean useTranslucentBackground, Context context) 
    {
	this.context = context;
        mTranslucentBackground = useTranslucentBackground;
	this.square = new Square();
    }
    @Override
    public void onDrawFrame(GL10 gl) 
    {
	synchronized(this)
	    {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		if (mTranslucentBackground) {
		    gl.glClearColor(0,0,0,0);
		} else {
		    gl.glClearColor(1,1,1,1);
		}
		gl.glLoadIdentity();

		gl.glTranslatef(0.0f, 0.0f, -5.0f);
		gl.glScalef(scale,scale,scale);
		gl.glRotatef(xangle,1f,0f,0f);
		gl.glRotatef(yangle,0f,1f,0f);
		gl.glRotatef(zangle,0f,0f,1f);
		
		square.draw(gl);
	    }
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
	Log.i("SphereActivity", "CubeRenderer.onSurfaceChanged");
	if(height == 0) {
	    height = 1;
	}
	gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
	gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
	gl.glLoadIdentity(); 					//Reset The Projection Matrix
	
	//Calculate The Aspect Ratio Of The Window
	GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
	
	gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
	gl.glLoadIdentity(); 					//Reset The Modelview Matrix
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
	Log.i("SphereActivity", "CubeRenderer.onSurfaceCreated");
	// Load the texture for the square
	square.loadGLTexture(gl, this.context);
	
	gl.glEnable(GL10.GL_TEXTURE_2D);			//Enable Texture Mapping ( NEW )
	gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
	gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
	gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
	gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
	gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
	
	//Really Nice Perspective Calculations
	gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}
