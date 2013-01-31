package com.web2rev.droid.Sphere;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.provider.MediaStore.Images.Media;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
public class CameraControl
{
    protected static final String TAG = "CameraControl";
    protected static Camera camera;
    protected static boolean cameraOpened=false;
    protected boolean closeCalled = false;
    protected boolean previewStarted = false;
    protected boolean previewHolderSet = false;
    protected int cameraCounter = 0;
    public Camera.Parameters cameraParams;

    protected Camera.AutoFocusCallback autoFocusCallBack = 
	new AutoFocusCallback() {
                @Override
		public void onAutoFocus(boolean success, Camera camera) {;}
        };
    public CameraControl()
    {
	if(!cameraOpened)
	    {
		closeCalled = false;
		cameraOpened=true;
		camera = Camera.open();
		cameraParams = camera.getParameters();
		if(cameraParams.getFocusMode().contentEquals(
			  Camera.Parameters.FOCUS_MODE_FIXED) == false) 
		    {	
			camera.autoFocus(autoFocusCallBack);
		    }
	    }
    }
    public void TakePicture() 
    {
	camera.takePicture(shutterCallBack, rawCallBack, pictureCallBack);
    }
    public void setHolder(SurfaceHolder holder) {
	try
	    {
	camera.setPreviewDisplay(holder);
	previewHolderSet = true;
	    }
	catch(Exception ioe){
	    Log.d(TAG, "*** setHolder Exception ***");
	}
    }
    public void startPreview() {
	if(previewHolderSet == true) {
	    camera.setPreviewCallback(previewCallBack);                              
	    camera.startPreview();
	    previewStarted = true;
        }
    }        
    public void stopPreview() {
	if(previewStarted == true) {
	    camera.stopPreview();
	    previewStarted = false;
	}
    }
    protected void finalize() {
	Close();
    }
    public void Close() {           
	if(closeCalled == false) {
	    if(previewStarted == true) {
		camera.stopPreview();
		previewStarted = false;
	    }
	    if(cameraOpened) {
		cameraOpened=false;
		camera.release();
		camera = null;
	    }
	    closeCalled = true;
	}
    }
    private Camera.ShutterCallback shutterCallBack = new Camera.ShutterCallback() {
	    @Override
                public void onShutter() {
		// Visual / Audio of picture taken
                }               
	};
    private Camera.PictureCallback rawCallBack = new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                        if(data != null) {
			    ;
                        }
                }               
        };
    private Camera.PictureCallback pictureCallBack = new PictureCallback() {
	    @Override
                public void onPictureTaken(byte[] data, Camera camera) {
		if(data != null) {
		    if(previewStarted == true) {
			Bitmap bitmap
			    = BitmapFactory.decodeByteArray(
				  data, 0, data.length);
			int width= bitmap.getWidth();
			int height=bitmap.getHeight();
			float scaleWidth=((float)256)/width;
			float scaleHeight=((float)256)/height;
			Matrix matrix=new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			SphereActivity.mainActivity.loadedBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false);
			SphereActivity.mainActivity.ll.removeAllViews();
			SphereActivity.mainActivity.createMainView();
			SphereActivity.mainActivity.setContentView(SphereActivity.ll);
			SphereActivity.mainCameraSurface.Close();
			SphereActivity.mainCameraControl.Close();
			//camera.startPreview();
		    }
		}
	    }
	};
    private Camera.PreviewCallback previewCallBack = new Camera.PreviewCallback(){
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                        if(data != null) {
			    ;
                        }
		}
	   };
    public boolean IsCameraClosed() {
	return closeCalled;
    }
    public boolean IsPreviewRun() {
	return previewStarted;
    }   
}
