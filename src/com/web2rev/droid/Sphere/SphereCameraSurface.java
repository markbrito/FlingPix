package com.web2rev.droid.Sphere;
import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
public class SphereCameraSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "SphereCameraSurface";
    public SurfaceHolder m_holder = null;
    private CameraControl m_control = null;
    private boolean m_close_is_called = true;
    public SphereCameraSurface(Context context, boolean bCreateControl) {
	super(context);
                if(bCreateControl) {
		    CreateControl();
                }
                m_holder = this.getHolder();
                m_holder.addCallback(this);
                m_holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    protected void finalize() {
	Close();
    }
    public void CreateControl() {
	if(m_control == null) {
	    SphereActivity.mainCameraSurface=this;
	    SphereActivity.mainCameraControl= m_control = new CameraControl();
	    m_close_is_called = false;
	}
    }
    public boolean IsOpen()
    {
	return !m_close_is_called;
    }
    public void Close() {
	if(m_close_is_called == false) {
	    if(m_control.IsPreviewRun() == true) {
		m_control.stopPreview();
	    }
	    if(m_control.IsCameraClosed() == false) {
		m_control.Close();
		m_control = null;
		}
	    m_close_is_called = true;
	}
    }
    public CameraControl getControl() {
	return m_control;
    }
    @Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	Log.d(TAG, "*** surfaceChanged >>>>> ***");
	Log.d(TAG, "format=" + format + ", width=" + width + ", height=" + height);
	if(m_control.IsCameraClosed() == false) {
		    if(m_control.IsPreviewRun() == false) {                         
			m_control.startPreview();
		    }
	}
	Log.d(TAG, "*** surfaceChanged <<<<< ***");
    }
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
	Log.d(TAG, "*** surfaceCreated >>>>> ***");
	CreateControl();
	m_control.setHolder(holder);
	Log.d(TAG, "*** surfaceCreated <<<<< ***");
    }
    @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
	Log.d(TAG, "*** surfaceDestroyed >>>>> ***");
	if(m_control != null) {
	    if(m_control.IsCameraClosed() == false) {
		if(m_control.IsPreviewRun() == true) {
		    m_control.stopPreview();
		    }
	    }
	}
	Log.d(TAG, "*** surfaceDestroyed <<<<< ***");
    }
}