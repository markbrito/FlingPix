package com.web2rev.droid.Sphere;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.content.Context;
import android.os.Looper;
import android.widget.*;
import android.view.*;
import android.widget.LinearLayout;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.util.Log;
import android.provider.MediaStore.Images.Media;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.lang.Exception;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.Sensor;
public class SphereActivity extends Activity implements OnGestureListener,SensorEventListener
{
    public static final String TAG = "SphereActivity";
    public static final int MENU_ITEM_ACCEL=0;
    public static final int MENU_ITEM_GYRO=1;
    public static final int MENU_ITEM_GRAVITY=2;
    public static final int MENU_ITEM_LINACCEL=3;
    public static final int MENU_ITEM_ROTVECT=4;
    public static final int MENU_ITEM_MAGNETIC=5;
    public static final int MENU_ITEM_ORIENTATION=6;
    public static final int MENU_ITEM_CAMERA=7;
    public static final int MENU_ITEM_ABOUT=8;
    public static final int SELECT_PICTURE = 1;
    public static final long TRANSFORM_SLEEP=10;
    public static final long SCALE_SLEEP=50;
    public static final long RANDOM_SLEEP=0;
    public static SphereActivity mainActivity;
    public static CubeRenderer cubeRenderer=null;
    public static Bitmap loadedBitmap=null;
    public static SphereCameraSurface mainCameraSurface;
    public static CameraControl mainCameraControl;
    public static LinearLayout ll;
    protected GLSurfaceView mGLSurfaceView;
    protected SphereCameraSurface cameraSurface;
    protected ImageButton bzoomin;
    protected ImageButton bzoomout;
    protected ImageButton bx;
    protected ImageButton by;
    protected ImageButton bz;
    protected ImageButton bxn;
    protected ImageButton byn;
    protected ImageButton bzn;
    protected ImageButton bOpenPic;
    protected ImageButton bEffects;
    protected ImageButton bTakePic;
    protected ImageButton bSavePic;
    protected LinearLayout llLoadTakePic;
    protected LinearLayout llb;
    protected Bitmap bbitmap;
    protected int ICON_WIDTH=64;
    protected int ICON_HEIGHT=64;
    protected float BTN_WIDTH=1f;
    protected float BTN_HEIGHT=1f;
    protected DisplayMetrics display;
    protected Matrix bmatrix;
    private GestureDetector gestureScanner;
    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;
    private Sensor mAccelerometerSensor;
    private Sensor mMagneticSensor;
    private Object accelerometerMutex=new Object();
    private boolean accelerometerRunning=false;
    private Thread accelerometerThread;
    private final int accelSpinSteps = 20;
    private float accelX=0;
    private float accelY=0;
    private float accelZ=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagneticSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	mainActivity=this;
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	mainActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	gestureScanner = new GestureDetector(this);
	display = new DisplayMetrics();
	ll=new LinearLayout(this);
	ll.setOrientation(1);
	ll.setGravity(Gravity.BOTTOM | Gravity.CENTER);
	bzoomin=new ImageButton(this);
	bzoomin.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.zoomin);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bzoomin.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bzoomin.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bzoomin)
				    {
					SphereActivity.cubeRenderer.dScale=SphereActivity.cubeRenderer.INIT_DSCALE;
					while(SphereActivity.cubeRenderer.dScale>0){
					    SphereActivity.cubeRenderer.scale+= SphereActivity.cubeRenderer.dScale;
					    SphereActivity.cubeRenderer.dScale=
						SphereActivity.cubeRenderer.dScale-SphereActivity.cubeRenderer.ddScale<0?0:
					  SphereActivity.cubeRenderer.dScale-SphereActivity.cubeRenderer.ddScale;
					    try {
						Thread.sleep(SCALE_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
				    }
			    }
			}).start();
		}
	    });
	bzoomout=new ImageButton(this);
	bzoomout.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.zoomout);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);	
	bzoomout.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bzoomout.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bzoomout)
				    {
					SphereActivity.cubeRenderer.dScale=SphereActivity.cubeRenderer.INIT_DSCALE;
					while(SphereActivity.cubeRenderer.dScale>0){
					    SphereActivity.cubeRenderer.scale-= SphereActivity.cubeRenderer.dScale;
					    SphereActivity.cubeRenderer.dScale=
					  SphereActivity.cubeRenderer.dScale-SphereActivity.cubeRenderer.ddScale<0?0:
						SphereActivity.cubeRenderer.dScale-SphereActivity.cubeRenderer.ddScale;
					    try {
						Thread.sleep(SCALE_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
				    }
			    }
			}).start();
		}
	    });
	bx=new ImageButton(this);
	bx.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.xrotationdown);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);	
	bx.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bx.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bx)
				    {
					while(SphereActivity.cubeRenderer.xangle<360){
					    SphereActivity.cubeRenderer.xangle+=SphereActivity.cubeRenderer.dxangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.xangle=0;
				    }
			    }
			}).start();
		}
	    });
	bxn=new ImageButton(this);
	bxn.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.xrotationup);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bxn.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bxn.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bx)
				    {
					while(SphereActivity.cubeRenderer.xangle>-360){
					    SphereActivity.cubeRenderer.xangle-=SphereActivity.cubeRenderer.dxangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.xangle=0;
				    }
			    }
			}).start();
		}
	    });
	by=new ImageButton(this);
	by.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.yrotationright);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	by.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	by.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(by)
				    {
					while(SphereActivity.cubeRenderer.yangle<360){
					    SphereActivity.cubeRenderer.yangle+=SphereActivity.cubeRenderer.dyangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.yangle=0;
				    }
			    }
			}).start();
		}
	    });
	byn=new ImageButton(this);
	byn.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.yrotationleft);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	byn.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	byn.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(by)
				    {
					while(SphereActivity.cubeRenderer.yangle>-360){
					    SphereActivity.cubeRenderer.yangle-=SphereActivity.cubeRenderer.dyangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.yangle=0;
				    }
			    }
			}).start();
		}
	    });
	bz=new ImageButton(this);
	bz.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.zrotationpositive);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bz.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bz.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bz)
				    {
					while(SphereActivity.cubeRenderer.zangle<360){
					    SphereActivity.cubeRenderer.zangle+=SphereActivity.cubeRenderer.dzangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.zangle=0;
				    }
			    }
			}).start();
		}
	    });
	bzn=new ImageButton(this);
	bzn.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.zrotationnegative);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bzn.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bzn.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bz)
				    {
					while(SphereActivity.cubeRenderer.zangle>-360){
					    SphereActivity.cubeRenderer.zangle-=SphereActivity.cubeRenderer.dzangle;
					    try {
						Thread.sleep(TRANSFORM_SLEEP+(long)(Math.random()*RANDOM_SLEEP));
					    }catch(InterruptedException ie){;}
					}
					SphereActivity.cubeRenderer.zangle=0;
				    }
			    }
			}).start();
		}
	    });
	bOpenPic=new ImageButton(this);
	bOpenPic.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.openpicture);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bOpenPic.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bOpenPic.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bOpenPic)
				    {
					Intent intent= new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
				    }
			    }
			}).start();
		}
	    });
	bEffects=new ImageButton(this);
	bEffects.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.effects);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bEffects.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bEffects.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bEffects)
				    {
					Intent intent= new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
				    }
			    }
			}).start();
		}
	    });
	bTakePic=new ImageButton(this);
	bTakePic.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.camera);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bTakePic.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bTakePic.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    cameraSurface = new SphereCameraSurface(mainActivity,false);
		    setContentView(cameraSurface);
		}
	    });
	bSavePic=new ImageButton(this);
	bSavePic.setBackgroundColor(Color.argb(255, 255, 255, 255));
	bbitmap = BitmapFactory.decodeResource(getResources(),
					       R.drawable.camera);
	bmatrix=new Matrix();
	bmatrix.postScale(BTN_WIDTH,BTN_HEIGHT);
	bSavePic.setImageDrawable(new BitmapDrawable(Bitmap.createBitmap(bbitmap, 0, 0, ICON_WIDTH, ICON_HEIGHT,bmatrix, true)));
	bSavePic.setOnClickListener(new View.OnClickListener(){
		public void onClick(View v){
		    new Thread(new Runnable() {
			    public void run() {
				synchronized(bSavePic)
				    {
					Intent intent= new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
				    }
			    }
			}).start();
		}
	    });
	llb=new LinearLayout(this);
	llb.setOrientation(0);
	llb.setGravity(Gravity.BOTTOM | Gravity.CENTER);
	llb.addView(bTakePic);
	llb.addView(bzoomin);
	llb.addView(by);
	llb.addView(bx);
	llb.addView(bzn);
	llLoadTakePic=new LinearLayout(this);
	llLoadTakePic.setOrientation(0);
	llLoadTakePic.setGravity(Gravity.CENTER);
	llLoadTakePic.addView(bOpenPic);
	llLoadTakePic.addView(bzoomout);
	llLoadTakePic.addView(byn);
	//	llLoadTakePic.addView(bEffects);
	llLoadTakePic.addView(bxn);
	//	llLoadTakePic.addView(bSavePic);
	llLoadTakePic.addView(bz);
	createMainView();
	setContentView(ll);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
	super.onActivityResult(requestCode,resultCode,data);
	if(requestCode == SELECT_PICTURE){
	    if(resultCode == Activity.RESULT_OK){
		Uri selectedImage = data.getData();
		try
		    {
			Bitmap bitmap=Media.getBitmap(getContentResolver(), selectedImage);
			int width= bitmap.getWidth();
			int height=bitmap.getHeight();
			float scaleWidth=((float)256)/width;
			float scaleHeight=((float)256)/height;
			Matrix matrix=new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			loadedBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false);
		    }
		catch(Exception ffe)
		    {
			Log.d(TAG, "*** onActivityResult Exception ***");
		    }
		ll.removeAllViews();
		createMainView();
	    }
	}
    }
    public void createMainView()
    {
	mGLSurfaceView = new GLSurfaceView(this);
	cubeRenderer=new CubeRenderer(true,this);
	mGLSurfaceView.setRenderer(cubeRenderer);
	ll.setBackgroundColor(Color.argb(255, 255, 255, 255));
	ll.addView(llb);
	ll.addView(llLoadTakePic);
	ll.addView(mGLSurfaceView);
    }
    protected void createLoadPictureView()
    {
	ll.addView(llLoadTakePic);
    }
    @Override
    protected void onResume() {
	Log.i("SphereActivity", "SphereActivity.onResume");
        super.onResume();
	// Maybe ask to load picture
	//	ll.removeAllViews();
	//	createLoadPictureView();
	mGLSurfaceView.onResume();
    }
    @Override
    protected void onPause() {
	Log.i("SphereActivity", "SphereActivity.onPause");
        super.onPause();
	mSensorManager.unregisterListener(this);
	if(cameraSurface!=null && cameraSurface.IsOpen() &&
	   cameraSurface.getControl() != null) {
	    if(cameraSurface.getControl().IsCameraClosed() == false) {
		if(cameraSurface.getControl().IsPreviewRun() == true) {
		    cameraSurface.getControl().stopPreview();
		}
	    }
	}
        final boolean[] is_pausing = new boolean[1];
        synchronized (mGLSurfaceView) {
            mGLSurfaceView.onPause();
            mGLSurfaceView.queueEvent(new Runnable() {
                public final void run() {
                    synchronized (is_pausing) {
                        is_pausing[0] = true;
                        is_pausing.notify();
                    }
                }
            });
        }
        synchronized (is_pausing) {
            while (!is_pausing[0]) {
                try {is_pausing.wait();} catch (InterruptedException e) {}
            }
        }
    }
    @Override
        public void onDestroy() {
	super.onDestroy();
	if(cameraSurface!=null && cameraSurface.IsOpen())
	    cameraSurface.Close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ITEM_ABOUT, 0, R.string.menu_about);
        menu.add(0, MENU_ITEM_ACCEL, 1, R.string.menu_accel);
        menu.add(0, MENU_ITEM_GYRO, 2, R.string.menu_gyroscope);
        menu.add(0, MENU_ITEM_MAGNETIC, 3, R.string.menu_magnetic);
        menu.add(0, MENU_ITEM_CAMERA, 4, R.string.menu_camera);
	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case MENU_ITEM_ABOUT:
	    showPopup(getString(R.string.message_about));
	    break;
	case MENU_ITEM_GYRO:
	    mSensorManager.unregisterListener(this);
	    mSensorManager.registerListener(this, mGyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    break;
	case MENU_ITEM_ACCEL:
	    mSensorManager.unregisterListener(this);
	    mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    break;
	case MENU_ITEM_MAGNETIC:
	    mSensorManager.unregisterListener(this);
	    mSensorManager.registerListener(this, mMagneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
	    break;
	case MENU_ITEM_CAMERA:
	    mSensorManager.unregisterListener(this);
	    cameraSurface = new SphereCameraSurface(mainActivity,false);
	    setContentView(cameraSurface);
	    break;
	}
        return super.onOptionsItemSelected(item);
    }
    protected void showPopup(String message)
    {
        PopupWindow window = new PopupWindow(this);
        window.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        window.setTouchable(true);
        window.setFocusable(true);
        EditText text = new EditText(this);
        text.setText(message);
        window.setContentView(text);
        window.showAtLocation(text, Gravity.NO_GRAVITY, 30, 30);
    }
    @Override
    public boolean onTouchEvent(MotionEvent evt)
    {
	return gestureScanner.onTouchEvent(evt);
    }
    @Override
    public boolean onDown(MotionEvent e)
    {
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
	if (Math.abs( e1.getY() - e2.getY() ) <=30 && e1.getX()<e2.getX())
	    by.performClick();
	else if (Math.abs( e1.getY() - e2.getY() ) <=30 && e1.getX()>e2.getX())
	    byn.performClick();
	else if (Math.abs( e1.getX() - e2.getX() ) <=30 && e1.getY()< e2.getY())
	    bx.performClick();
	else if (Math.abs( e1.getX() - e2.getX() ) <=30 && e1.getY()> e2.getY())
	    bxn.performClick();
	else if(e1.getX()<e2.getX() && e1.getY()<e2.getY() && e1.getY()<e2.getY())
	    bz.performClick();
	else if(e1.getX()>e2.getX() && e1.getY()<e2.getY() && e1.getY()<e2.getY())
	    bzn.performClick();
        return true;
    }
    @Override
    public void onLongPress(MotionEvent e)
    {
	bzoomout.performClick();
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return true;
    }
    @Override
    public void onShowPress(MotionEvent e)
    {
    }    
    @Override
    public boolean onSingleTapUp(MotionEvent e)    
    {
	bzoomin.performClick();
        return true;
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
     }

     public void onSensorChanged(SensorEvent event) {
	 float rotX = 0;
	 float rotY = 0;
	 float rotZ = 0;
	 if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
	     {
		 rotX = event.values[0];
		 rotY = event.values[1];
		 rotZ = event.values[2];
		 SphereActivity.cubeRenderer.xangle-=(float)(4*rotX);
		 SphereActivity.cubeRenderer.yangle-=(float)(4*rotY);
		 SphereActivity.cubeRenderer.zangle-=(float)(10*rotZ);
	     }
	 if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)
	     {
		 rotX = event.values[1];
		 rotY = event.values[2];
		 rotZ = event.values[0];
		 SphereActivity.cubeRenderer.xangle=(float)rotX;
		 SphereActivity.cubeRenderer.yangle=(float)rotY;
		 SphereActivity.cubeRenderer.zangle=(float)rotZ;
	     }
	 if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
	     {
		 accelX = (float)(accelX + event.values[1])/(float)2.0;
		 accelY = (float)(accelY + event.values[0])/(float)2.0;
		 accelZ = (float)(accelY + event.values[2])/(float)2.0;
		 if(accelerometerRunning)
		     return;
		 if(accelX>9.0 || accelY>4.0 || accelZ>5.0)
		     {
		 synchronized(accelerometerMutex)
		     {
			 if(!accelerometerRunning)
			     {
				 accelerometerRunning=true;	 
				 accelerometerThread=new Thread(new Runnable() {
					 public void run() {
					     for(int accelIter=0;accelIter<=accelSpinSteps;++accelIter)
						 {
						     SphereActivity.cubeRenderer.xangle+=
							 (int)(accelX>9.0?accelX:0);
						     SphereActivity.cubeRenderer.yangle-=
							 (int)(accelY>4.0?accelY+2:0);
						     SphereActivity.cubeRenderer.zangle+=
						     	 (int)(accelZ>5.0?accelZ+2:0);
						 try {
						     Thread.sleep((long)(accelIter*4+TRANSFORM_SLEEP+Math.random()*RANDOM_SLEEP));
						 }catch(InterruptedException ie){;}
					     }
					     accelerometerRunning=false;
					 }
				     });
				 
				 accelerometerThread.start();
			     }
		     }
		     }
	     }
     }
}
