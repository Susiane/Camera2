package com.example.susi.camera2;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class DucumentacaoActivity extends AppCompatActivity {
    private Camera mCamera;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ducumentacao);

        safeCameraOpen();
    }

    private boolean safeCameraOpen() {
        boolean qOpened = false;

        try {
            releaseCameraAndPreview();

            mCamera = Camera.open(findFrontFacingCamera());
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Toast.makeText(this, "failed to open Camera", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return qOpened;
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("DEBUG_TAG", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void releaseCameraAndPreview() {
        mPreview.setCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public class Preview extends ViewGroup implements SurfaceHolder.Callback {
        List<Camera.Size> mSupportedPreviewSizes;

        SurfaceView mSurfaceView;
        SurfaceHolder mHolder;

        Preview(Context context) {
            super(context);

            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
            //parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPreviewSize(w, h);
            requestLayout();
            mCamera.setParameters(parameters);

            // Important: Call startPreview() to start updating the preview surface.
            // Preview must be started before you can take a picture.
            mCamera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        public void setCamera(Camera camera) {
            if (mCamera == camera) { return; }

            //-------> stopPreviewAndFreeCamera();

            mCamera = camera;

            if (mCamera != null) {
                List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                mSupportedPreviewSizes = localSizes;
                requestLayout();

                try {
                    mCamera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Important: Call startPreview() to start updating the preview
                // surface. Preview must be started before you can take a picture.
                mCamera.startPreview();
            }
        }
    }
}
