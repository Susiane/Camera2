package com.example.susi.camera2;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Susi on 9/29/17.
 */

public class CameraController implements SurfaceHolder.Callback {

    private android.hardware.Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private boolean previewing = false;
    private Activity atividade;

    public CameraController(Activity atividade, int surfaceView) {
        this.surfaceView = (SurfaceView) atividade.findViewById(surfaceView);
        surfaceHolder = this.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        this.atividade = atividade;
    }

    /**
     * Isto é chamado imediatamente após o SurfaceHolder ser criado.
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = android.hardware.Camera.open();
    }

    /**
     * Isto é chamado imediatamente após as alterações estruturais (o formato ou
     * tamanho) foram feitos ao SurfaceHolder.
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (previewing) {
            pararVisualizacao();
        }

        if (camera != null) {
            setCameraDisplayOrientation(atividade,camera);
            try {
                camera.setPreviewDisplay(surfaceHolder);
                iniciarVisualizacao();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Isto é chamado imediatamente antes do SurfaceHolder ser destruído.
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pararVisualizacao();
        camera.release();
        camera = null;
    }

    public void tirarFoto(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
        camera.takePicture(shutter, raw, jpeg);
    }

    public void iniciarVisualizacao() {
        previewing = true;
        camera.startPreview();
    }

    public void pararVisualizacao() {
        camera.stopPreview();
        previewing = false;
    }

    public Camera getCameraControler() {
        return camera;
    }

    public static void setCameraDisplayOrientation(Activity activity, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(findFrontFacingCamera(), info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                //Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

}
